import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
public class SimulationPanel extends JPanel{
    private List<Entity> entities = new ArrayList<>();

    private static final int ICON_SIZE = 25;
    private static final int GRASS_SIZE = 15;

    private final Image predatorIcon = loadAnimatedIcon("resources/tiger.gif");
    private final Image preyIcon = loadAnimatedIcon("resources/rabbit.gif");
    private final Image grassIcon = loadStaticIcon("resources/grass.png", GRASS_SIZE);

    private Image loadAnimatedIcon (String path){
        Image result = null;
        try {
            File file = new File(path);
        } catch (Exception e) {
        }

        return result;
    }

    private Image loadStaticIcon(String path, int size){
        Image result = null;
        try {
            
        } catch (Exception e) {
        }
        return result;
    }

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

            g.fillOval(entity.getX(), entity.getY(), 30, 30);
        }
    }
}