import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Predator-Prey Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int[] counts = askEntityCounts(frame);
        int numPredators = counts[0];
        int numPrey = counts[1];

        SimulationPanel simPanel = new SimulationPanel();
        Simulation sim = new Simulation(simPanel);
        try {
            java.util.Random rand = new java.util.Random();
            for (int i = 0; i < numPredators; i++) {
                int x = 50 + rand.nextInt(700);
                int y = 150 + rand.nextInt(350);
                sim.addEntity(new Predator(3, 100, x, y));
            }
            for (int i = 0; i < numPrey; i++) {
                int x = 50 + rand.nextInt(700);
                int y = 150 + rand.nextInt(350);
                sim.addEntity(new Prey(4, 100, false, x, y));
            }
            sim.addEntity(new Grass(100, 100));
            sim.addEntity(new Grass(300, 400));
            sim.addEntity(new Grass(500, 150));
        } catch (InvalidCreatureStateException e) {
            System.err.println("Failed to create initial entities: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Simulation could not start: " + e.getMessage(),
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            System.err.println("Entity setup attempt complete. Current entity count: " + sim.getEntities().size());
        }

        simPanel.setEntities(sim.getEntities());
        Timer timer = new Timer(33, e -> {
            sim.tick();
            simPanel.repaint();

            long predatorCount = sim.getEntities().stream().filter(en -> en instanceof Predator).count();
            long preyCount = sim.getEntities().stream().filter(en -> en instanceof Prey).count();

            if (predatorCount == 0 && preyCount == 0) {
                ((Timer) e.getSource()).stop();
                simPanel.setSimulationOver(true);
                simPanel.repaint();
            }
        });


        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> timer.start());
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> timer.stop());
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            timer.stop();
            sim.reset();
            simPanel.setSimulationOver(false);
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

    private static int[] askEntityCounts(JFrame parent) {
        JSpinner predatorSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
        JSpinner preySpinner = new JSpinner(new SpinnerNumberModel(4, 0, 20, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Number of predators (Max 5):"));
        panel.add(predatorSpinner);
        panel.add(new JLabel("Number of prey (Max 20):"));
        panel.add(preySpinner);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Simulation Setup",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        int predators = (Integer) predatorSpinner.getValue();
        int prey = (Integer) preySpinner.getValue();
        return new int[]{predators, prey};
    }
}