import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Simulation {
    private List<Entity> entities;
    private List<Entity> initialEntities = new ArrayList<>();
    private SimulationPanel panel;
    private static final int TICKS_PER_DAY = 152;
    private int tickCounter = 0;
    private Random random = new Random();

    private static final int PREDATOR_HUNT_RANGE = 200;
    private static final int PREY_FORAGE_RANGE = 100;
    private static final int PREY_THREAT_RANGE = 120;
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

    public void addEntity(Entity entity) {
        entities.add(entity);
        initialEntities.add(entity);
    }

    public void tick() {
        List<Prey> preyList = entities.stream()
            .filter(e -> e instanceof Prey)
            .map(e -> (Prey) e)
            .toList();

        List<Predator> predators = entities.stream()
            .filter(e -> e instanceof Predator)
            .map(e -> (Predator) e)
            .toList();

        List<Grass> grassList = entities.stream()
            .filter(e -> e instanceof Grass)
            .map(e -> (Grass) e)
            .filter(Grass::isEdible)
            .toList();

        List<Prey> availablePrey = new ArrayList<>(preyList);

        for (Predator predator : predators) {
            Entity nearestPrey = findNearest(predator, availablePrey, PREDATOR_HUNT_RANGE);
            predator.setTarget(nearestPrey);
            if (nearestPrey != null) {
                availablePrey.remove(nearestPrey);
            }
        }

        for (Prey prey : preyList) {
            Entity nearestGrass = findNearest(prey, grassList, PREY_FORAGE_RANGE);
            prey.setTarget(nearestGrass);
        }
        
        for(Prey prey: preyList){
            Predator nearestThreat = (Predator) findNearest(prey, predators, PREY_THREAT_RANGE);
            if(nearestThreat != null){
                prey.setInDanger(true);
                prey.setFleeTarget(nearestThreat);
            } else{
                prey.setInDanger(false);
            }
        }

        int width = panel.getWidth();
        int height = panel.getHeight();
        for (Entity entity : entities) {
            entity.update(width, height);
        }

        int groundTop = height/5;
        // Anti-merging logic (Separation) for Prey
        applySeparation(preyList, PREY_SEPARATION_DIST, width, height);
        applySeparation(predators, PREDATOR_SEPARATION_DIST, width, height);

        Set<Prey> consumedPrey = new HashSet<>();

        for (Predator predator : predators) {
            if (predator.isFedToday()) {
                continue;
            }
            for (Prey prey : preyList) {
                if (!consumedPrey.contains(prey) && predator.isNear(prey, EAT_RANGE)) {
                    predator.setHunger(predator.getHunger() + 50);
                    predator.setFedToday(true);
                    predator.startRetreat(prey.getX(), prey.getY());
                    prey.setHunger(0);
                    consumedPrey.add(prey);
                    break;
                }
            }
        }

        for (Prey prey : preyList) {
            for (Grass grass : grassList) {
                if (prey.isNear(grass, EAT_RANGE) && grass.isEdible()) {
                    prey.setHunger(prey.getHunger() + 30);
                    prey.setFedToday(true);
                    grass.setEaten(true);
                }
            }
        }

        // Remove dead creatures and grass that was just eaten this tick
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
        List<Entity> newborns = new ArrayList<>();
        int groundTop = panel.getHeight() / 5;
        int panelW = panel.getWidth();
        int panelH = panel.getHeight();

    for (Entity e : entities) {
        e.onDayTick();
        Entity offspring = e.reproduce(panelW, panelH, groundTop, random);
        if (offspring != null) {
            newborns.add(offspring);
        }
    }

    entities.removeIf(Entity::shouldBeRemoved);

        int groundHeight = panelH - groundTop - Grass.GRASS_SIZE;
        int newGrassCount = 5 + random.nextInt(6);
        for (int i = 0; i < newGrassCount; i++) {
            int x = random.nextInt(panelW - Grass.GRASS_SIZE);
            int y = groundTop + random.nextInt(Math.max(1, groundHeight));
            newborns.add(new Grass(x, y, 0));
        }
        entities.addAll(newborns);
    }

    public void reset() {
        entities.clear();
        entities.addAll(initialEntities);
        for (Entity entity : entities) {
            entity.setX(entity.getOriginalX());
            entity.setY(entity.getOriginalY());
            if (entity instanceof Creature c) {
                c.resetState(100, entity.getOriginalX(), entity.getOriginalY());
            }
            if (entity instanceof Grass g) {
                g.setEaten(false);
            }
        }
    }

    private Entity findNearest(Entity from, List<? extends Entity> candidates, int range) {
        Entity nearest = null;
        long maxDistSq = (long) range * range;
        long nearestDistSq = Long.MAX_VALUE;

        for (Entity candidate : candidates) {
            long dx = from.getX() - candidate.getX();
            long dy = from.getY() - candidate.getY();
            long distSq = dx * dx + dy * dy;

            if (distSq <= maxDistSq && distSq < nearestDistSq) {
                nearest = candidate;
                nearestDistSq = distSq;
            }
        }
        return nearest;
    }
}
