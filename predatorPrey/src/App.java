import java.awt.BorderLayout;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Predator-Prey Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SimulationPanel simPanel = new SimulationPanel();
        Simulation sim = new Simulation();
        sim.addEntity(new Predator(2, 100, 50, 50));
        sim.addEntity(new Prey(3, 100, false, 200, 200));
        simPanel.setEntities(sim.getEntities());
        Timer timer = new Timer(33, e -> {
            sim.tick();
            simPanel.repaint();
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> timer.start());
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> timer.stop());
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            timer.stop();
            sim.reset();
            simPanel.repaint();
        });

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
