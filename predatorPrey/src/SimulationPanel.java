import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class SimulationPanel extends JPanel{
    private List<Entity> entities = new ArrayList<>();

    public void setEntities(List<Entity> entities){
        this.entities = entities;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        for(Entity entity: entities){
            if(entity instanceof Predator){
                g.setColor(Color.RED);
            } else if(entity instanceof Prey){
                g.setColor(Color.BLUE);
            } else if (entity instanceof Grass){
                g.setColor(Color.GREEN);
            } else{
                g.setColor(Color.BLACK);
            }

            g.fillOval(entity.getX(), entity.getY(), 10, 10);
        }
    }
}
