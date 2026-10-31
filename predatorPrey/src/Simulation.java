import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
public class Simulation {
    private List<Entity> entities;
    private List<Entity> initialEntities = new ArrayList<>();
    private SimulationPanel panel;
    private static final int TICKS_PER_DAY = 152;
    private int tickCounter = 0;
    private Random random = new Random();
    private static final int PREDATOR_HUNT_RANGE = 200;
    private static final int PREY_FORAGE_RANGE = 100;
    private static final int PREY_THREAT_RANGE = 100;
    private static final int EAT_RANGE = 35;
    private static final int PREY_SEPARATION_DIST = 15;
    private static final int PREDATOR_SEPARATION_DIST = 20;
    public static final int SPRITE_MARGIN = 65;

    public Simulation(SimulationPanel panel) {
        this.entities = new ArrayList<>();
        this.panel = panel;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Map<String, Long> getPopulationBySpecies() {
        return entities.stream()
            .collect(Collectors.groupingBy(
                entity -> entity.getClass().getSimpleName(),
                TreeMap::new,
                Collectors.counting()
            ));
    }

    public Map<String, HungerSummary> getHungerBySpecies() {
        return entities.parallelStream()
            .filter(e -> e instanceof Creature)
            .map(Creature.class::cast)
            .collect(Collectors.groupingBy(
                c -> c.getClass().getSimpleName(),
                TreeMap::new,
                SimulationCollectors.hungerSummary()));
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        initialEntities.add(entity);
    }

    private <T extends Entity> List<T> ofType(Class<T> type) {
        return entities.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .toList();
    }

    public void tick() {
        List<Prey> preyList = ofType(Prey.class);
        List<Predator> predators = ofType(Predator.class);
        List<Grass> grassList = ofType(Grass.class).stream().filter(Grass::isEdible).toList();
        List<Prey> availablePrey = new ArrayList<>(preyList);
        // Order-dependent greedy assignment: each predator removes its pick from the pool
        for (Predator predator : predators) {
            Entity nearestPrey = findNearest(predator, availablePrey, PREDATOR_HUNT_RANGE);
            predator.setTarget(nearestPrey);
            if (nearestPrey != null) {
                availablePrey.remove(nearestPrey);
            }
        }
        preyList.forEach(prey -> {
            prey.setTarget(findNearest(prey, grassList, PREY_FORAGE_RANGE));
            Predator threat = (Predator) findNearest(prey, predators, PREY_THREAT_RANGE);
            prey.setInDanger(threat != null);
            if (threat != null) {
                prey.setFleeTarget(threat);
            }
        });

        int width = panel.getWidth();
        int height = panel.getHeight();
        entities.forEach(entity -> entity.update(width, height));
        // Anti-merging logic (Separation)
        applySeparation(preyList, PREY_SEPARATION_DIST, width, height);
        applySeparation(predators, PREDATOR_SEPARATION_DIST, width, height);

        entities.stream()
            .filter(Creature.class::isInstance)
            .map(Creature.class::cast)
            .forEach(Creature::finishMovementTick);
        Set<Prey> consumedPrey = new HashSet<>();

        predators.stream()
            .filter(predator -> !predator.isFedToday())
            .forEach(predator -> preyList.stream()
                .filter(prey -> !consumedPrey.contains(prey) && predator.isNear(prey, EAT_RANGE))
                .findFirst()
                .ifPresent(prey -> {
                    predator.setHunger(predator.getHunger() + 50);
                    predator.setFedToday(true);
                    predator.startRetreat(prey.getX(), prey.getY());
                    prey.setHunger(0);
                    consumedPrey.add(prey);
                }));

        preyList.stream()
            .filter(prey -> !consumedPrey.contains(prey))
            .forEach(prey -> grassList.stream()
                .filter(grass -> grass.isEdible() && prey.isNear(grass, EAT_RANGE))
                .forEach(grass -> {
                    prey.setHunger(prey.getHunger() + 30);
                    prey.setFedToday(true);
                    grass.setEaten(true);
                }));
                
        entities.removeIf(e -> e instanceof Creature && ((Creature) e).isDead());
        entities.removeIf(e -> e instanceof Grass g && g.isMarkedForRemoval());

        tickCounter++;
        if (tickCounter >= TICKS_PER_DAY) {
            tickCounter = 0;
            advanceDay();
        }
    }

    private void applySeparation(List<? extends Creature> creatures, int minDistance, int panelWidth, int panelHeight) {
        int groundTop = panelHeight / 5;
        for (int i = 0; i < creatures.size(); i++) {
            for (int j = i + 1; j < creatures.size(); j++) {
                Creature c1 = creatures.get(i);
                Creature c2 = creatures.get(j);
                if (c1.isNear(c2, minDistance)) {
                    int dx = c1.getX() - c2.getX();
                    int dy = c1.getY() - c2.getY();
                    // If identical coordinates, pick a random separation direction
                    if (dx == 0 && dy == 0) {
                        dx = random.nextBoolean() ? 1 : -1;
                        dy = random.nextBoolean() ? 1 : -1;
                    }
                    c1.setX(clamp(c1.getX() + (int) Math.signum(dx) * 2, 0, panelWidth - SPRITE_MARGIN));
                    c1.setY(clamp(c1.getY() + (int) Math.signum(dy) * 2, groundTop, panelHeight - SPRITE_MARGIN));
                    c2.setX(clamp(c2.getX() - (int) Math.signum(dx) * 2, 0, panelWidth - SPRITE_MARGIN));
                    c2.setY(clamp(c2.getY() - (int) Math.signum(dy) * 2, groundTop, panelHeight - SPRITE_MARGIN));
                }
            }
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private void advanceDay() {
        int groundTop = panel.getHeight() / 5;
        int panelW = panel.getWidth();
        int panelH = panel.getHeight();
        entities.forEach(Entity::onDayTick);
        List<Entity> newborns = entities.stream()
            .map(e -> e.reproduce(panelW, panelH, groundTop, random))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
        entities.removeIf(Entity::shouldBeRemoved);

        int groundHeight = panelH - groundTop - Grass.GRASS_SIZE;
        int newGrassCount = 5 + random.nextInt(6);
        IntStream.range(0, newGrassCount)
            .mapToObj(i -> new Grass(
                random.nextInt(panelW - Grass.GRASS_SIZE),
                groundTop + random.nextInt(Math.max(1, groundHeight))
            ))
            .forEach(newborns::add);
        entities.addAll(newborns);
    }

    public void reset() {
        tickCounter = 0;
        entities.clear();
        entities.addAll(initialEntities);
        for (Entity entity : entities) {
            entity.setX(entity.getOriginalX());
            entity.setY(entity.getOriginalY());

            if (entity instanceof Creature creature) {
                creature.resetState(
                    100,
                    entity.getOriginalX(),
                    entity.getOriginalY()
                );
            }

            if (entity instanceof Grass grass) {
                grass.resetState();
            }
        }
    }

    private Entity findNearest(Entity from, List<? extends Entity> candidates, int range) {
        long maxDistSq = (long) range * range;
        return candidates.stream()
            .filter(c -> distSq(from, c) <= maxDistSq)
            .min(Comparator.comparingLong(c -> distSq(from, c)))
            .orElse(null);
    }

    private static long distSq(Entity a, Entity b) {
        long dx = a.getX() - b.getX();
        long dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }
}