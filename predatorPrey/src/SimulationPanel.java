import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
            ImageIO.read(file);
            ImageIcon icon = new ImageIcon(path);
            result = icon.getImage();
        } catch (IOException e) {
            System.err.println("Failed to load animated icon ' " + path + "': " + e.getMessage());
        } finally{
            System.out.println("Finished icon load attempt for: " + path);
        }
        return result;
    }

    private Image loadStaticIcon(String path, int size){
        Image result = null;
        try {
            Image raw = ImageIO.read(new File(path));
            result = raw.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.println("Failed to load static icon '" + path + "': "+ e.getMessage());
        } finally{
            System.out.println("Finished icon load attempt for: "+ path);
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
            Image icon;
            int size;
            Color fallbackColor;
            if(entity instanceof Predator){
                icon = predatorIcon;
                size = ICON_SIZE;
                fallbackColor=Color.RED;
            } else if(entity instanceof Prey){
                icon = preyIcon;
                size = ICON_SIZE;
                fallbackColor = Color.BLUE;
            } else if (entity instanceof Grass){
                icon = grassIcon;
                size = GRASS_SIZE;
                fallbackColor = Color.GREEN;
            } else{
                icon = null;
                size = ICON_SIZE;
                fallbackColor=Color.BLACK;
            }

            if(icon!=null){
                g.drawImage(icon, entity.getX(), entity.getY(), size, size, this); 
            } else{
                g.setColor(fallbackColor);
                g.fillOval(entity.getX(), entity.getY(), size, size);
            }
        }
    }
}