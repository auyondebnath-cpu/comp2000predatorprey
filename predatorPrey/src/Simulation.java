import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Simulation {
    private List<Entity> entities;
    private List<Entity> initialEntities = new ArrayList<>();
    private SimulationPanel panel;
    private static final int TICKS_PER_DAY = 303;
    private int tickCounter = 0;

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
        int width = panel.getWidth();
        int height = panel.getHeight();
        for(Entity entity : entities){
            entity.update(width, height);
        }

        Set<Prey> consumedPrey = new HashSet<>();

        List<Prey> preyList = entities.stream().filter(e-> e instanceof Prey).map(e -> (Prey) e).toList();

        List<Predator> predators = entities.stream().filter(e -> e instanceof Predator).map(e-> (Predator) e).toList();

        List<Grass> grassList = entities.stream().filter(e -> e instanceof Grass).map(e -> (Grass) e).filter(Grass::isEdible).toList();

        for(Predator predator : predators){
            for(Prey prey: preyList){
                if(!consumedPrey.contains(prey) && predator.isNear(prey, 15)){
                    predator.setHunger(predator.getHunger()+50);
                    predator.setFedToday(true);
                    prey.setHunger(0);
                    consumedPrey.add(prey);
                }
            }
        }

        for (Prey prey: preyList){
            for(Grass grass: grassList){
                if(prey.isNear(grass, 15) && grass.isEdible()){
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
}
