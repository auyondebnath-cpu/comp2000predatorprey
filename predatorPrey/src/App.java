import java.awt.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Predator-Prey Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SimulationPanel simPanel = new SimulationPanel();

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);

        frame.setLayout(new BorderLayout());
        frame.add(simPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
