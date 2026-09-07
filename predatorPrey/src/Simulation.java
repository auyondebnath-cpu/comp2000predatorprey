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
        entities.removeIf(e -> e instanceof Creature && ((Creature) e).isDead());

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
    }

    public void reset(){
        for(Entity entity: entities){
            entity.setX(entity.getOriginalX());
            entity.setY(entity.getOriginalY());
        }
    }
}
