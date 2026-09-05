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
    }
}
