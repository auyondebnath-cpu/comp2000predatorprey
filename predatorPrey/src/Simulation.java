import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
public class Simulation {
    private List<Entity> entities;
    private List<Entity> initialEntities = new ArrayList<>();
    private SimulationPanel panel;
    private static final int TICKS_PER_DAY = 303;
    private int tickCounter = 0;
    private Random random = new Random();


    public Simulation(SimulationPanel panel){
        this.entities = new ArrayList<>();
        this.panel = panel;
    }


    public List<Entity> getEntities(){
        return entities;
    }


    public void addEntity(Entity entity){
        entities.add(entity);
        initialEntities.add(entity);
    }


    public void tick(){
        List<Prey> preyList = entities.stream().filter(e-> e instanceof Prey).map(e -> (Prey) e).toList();


        List<Predator> predators = entities.stream().filter(e -> e instanceof Predator).map(e-> (Predator) e).toList();


        List<Grass> grassList = entities.stream().filter(e -> e instanceof Grass).map(e -> (Grass) e).filter(Grass::isEdible).toList();


        for(Predator predator : predators){
            Entity nearestPrey = findNearest(predator, preyList, 200);
            predator.setTarget(nearestPrey);
        }


        for(Prey prey : preyList){
            Entity nearestGrass = findNearest(prey, grassList, 100);
            prey.setTarget(nearestGrass);
        }


        int width = panel.getWidth();
        int height = panel.getHeight();
        for(Entity entity : entities){
            entity.update(width, height);
        }


        Set<Prey> consumedPrey = new HashSet<>();


       
        for(Predator predator : predators){
            for(Prey prey: preyList){
                if(!consumedPrey.contains(prey) && predator.isNear(prey, 25)){
                    predator.setHunger(predator.getHunger()+50);
                    predator.setFedToday(true);
                    prey.setHunger(0);
                    consumedPrey.add(prey);
                }
            }
        }


        for (Prey prey: preyList){
            for(Grass grass: grassList){
                if(prey.isNear(grass, 35) && grass.isEdible()){
                    prey.setHunger(prey.getHunger() + 30);
                    prey.setFedToday(true);
                    grass.setEaten(true);
                }
            }        
        }


        entities.removeIf(e -> e instanceof Creature && ((Creature) e).isDead());


        entities.removeIf(e -> e instanceof Grass && ((Grass) e).isEaten());


        tickCounter++;
        if(tickCounter >= TICKS_PER_DAY){
            tickCounter = 0;
            advanceDay();
        }
    }


    private void advanceDay(){
        List<Entity> newborns = new ArrayList<>();


        for(Entity e: entities){
            if(e instanceof Predator p){
                p.onDayTick();
                if(p.shouldReproduce(5)){
                    newborns.add(new Predator(p.getSpeed(), 100, p.getX(), p.getY()));
                }
            } else if (e instanceof Prey p){
                p.onDayTick();
                if(p.shouldReproduce(3)){
                    newborns.add(new Prey(p.getSpeed(), 100, false, p.getX(), p.getY()));
                }
            } else if(e instanceof Grass g){
                g.onDayTick();
            }
        }


        entities.removeIf(e -> e instanceof Creature c && c.isStarved(4));


        entities.removeIf(e -> e instanceof Grass g && g.isMarkedForRemoval());


        int newGrassCount = 5 + random.nextInt(6);


        for(int i =0; i<newGrassCount; i++){
            newborns.add(new Grass(random.nextInt(panel.getWidth()), random.nextInt(panel.getHeight()), 0));
        }
        entities.addAll(newborns);
    }


    public void reset(){
        entities.clear();
        entities.addAll(initialEntities);
        for(Entity entity: entities){
            entity.setX(entity.getOriginalX());
            entity.setY(entity.getOriginalY());
            if(entity instanceof Creature c){
                c.setHunger(100);
            }
            if(entity instanceof Grass g){
                g.setEaten(false);
            }
        }
    }


    private Entity findNearest (Entity from, List<? extends Entity> candidates, int range){
        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for(Entity candidate : candidates){
            double dx = from.getX() - candidate.getX();
            double dy = from.getY() - candidate.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);
            if(dist<= range && dist <nearestDist){
                nearest = candidate;
                nearestDist = dist;
            }
        }
        return nearest;
    }
}
