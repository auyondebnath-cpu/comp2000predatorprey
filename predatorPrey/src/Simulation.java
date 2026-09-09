import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Entity> entities;

    public Simulation(){
        entities = new ArrayList<>();
    }

    public List<Entity> getEntities(){
        return entities;
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public void tick(){
        for(Entity entity : entities){
            entity.update();
        }

        List<Prey> preyList = entities.stream().filter(e-> e instanceof Prey).map(e -> (Prey) e).toList();

        List<Predator> predators = entities.stream().filter(e -> e instanceof Predator).map(e-> (Predator) e).toList();

        for(Predator predator: predators){
            for(Prey prey: preyList){
                if(predator.isNear(prey, 15)){
                    predator.setHunger(predator.getHunger()+50);
                    prey.setHunger(0);
                }
            }
        }

        List<Grass> grassList = entities.stream().filter(e -> e instanceof Grass).map(e -> (Grass) e).filter(Grass::isEdible).toList();

        for(Prey prey: preyList){
            for(Grass grass: grassList){
                if(prey.isNear(grass, 15) && grass.isEdible()){
                    prey.setHunger(prey.getHunger() + 30);
                    grass.setEaten(true);
                }
            }
        }

        entities.removeIf(e -> e instanceof Creature && ((Creature) e).isDead());

        entities.removeIf(e -> e instanceof Grass && ((Grass) e).isEaten());
    }

    public void reset(){
        for(Entity entity: entities){
            entity.setX(entity.getOriginalX());
            entity.setY(entity.getOriginalY());
        }
    }
}
