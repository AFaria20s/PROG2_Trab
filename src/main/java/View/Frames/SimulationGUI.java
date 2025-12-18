package View.Frames;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.SimulationConfig;
import View.Dialogs.ConfigDialog;
import View.Panels.ControlPanel;
import View.Panels.SimulationPanel;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class SimulationGUI {
    private Ecosystem ecosystem;
    private JFrame frame;
    private SimulationPanel simPanel;
    private ControlPanel controlPanel;
    private Timer timer;

    private int stepsRemaining = -1; // -1 significa infinito
    private OrganismType extinctionTarget = null; // null significa qualquer extinção

    public SimulationGUI() {
        setupLookAndFeel();
        initEcosystem(SimulationConfig.getInstance().getWIDTH(), SimulationConfig.getInstance().getHEIGHT());
        setupFrame();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf()); // ou FlatLightLaf()
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initEcosystem(int w, int h) {
        SimulationConfig.getInstance().setWIDTH(w);
        SimulationConfig.getInstance().setHEIGHT(h);
        this.ecosystem = new Ecosystem(w, h);
        this.ecosystem.initGrid();
    }

    private void setupFrame() {
        frame = new JFrame("EcoSimulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Painel Central
        simPanel = new SimulationPanel(ecosystem);
        frame.add(simPanel, BorderLayout.CENTER);

        // Painel Lateral dentro de um JScrollPane para
        // Possibilitar dar scroll caso seja maior do que o ecra
        controlPanel = new ControlPanel(this);
        JScrollPane scrollPane = new JScrollPane(controlPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frame.add(scrollPane, BorderLayout.EAST);

        // Configurar Timer
        int delay = 1000 / SimulationConfig.getInstance().getSTEPS_PER_SECOND();
        timer = new Timer(delay, e -> executeStepLoop());

        // Setup Final Janela
        frame.setPreferredSize(new Dimension(1100, 750));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        updateUI();
    }

    // --- Simulacao ---

    private void executeStepLoop() {
        // Verifica se o ecossistema morreu
        if (!ecosystem.isLifeActive()) {
            stopTimerAndNotify("💀 EXTINÇÃO TOTAL 💀\nO ecossistema colapsou.");
            return;
        }

        // Verificacao - Até Extinção de Espécie X
        if (extinctionTarget != null) {
            if (ecosystem.getOrganismCountByType(extinctionTarget) == 0) {
                stopTimerAndNotify("A espécie " + extinctionTarget.asString() + " foi extinta!");
                return;
            }
        }

        // Executa o passo
        ecosystem.simulateStep();
        updateUI();

        // Verificacao - N Passos
        if (stepsRemaining > 0) {
            stepsRemaining--;
            if (stepsRemaining == 0) {
                timer.stop();
                stepsRemaining = -1; // Reset
                updateUI();
                JOptionPane.showMessageDialog(frame, "Simulação de N passos concluída.");
            }
        }
    }

    private void stopTimerAndNotify(String message) {
        timer.stop();
        stepsRemaining = -1;
        extinctionTarget = null;
        updateUI();
        JOptionPane.showMessageDialog(frame, message);
    }

    // --- Metodos Publicos ---

    public void runOneStep() {
        if (!timer.isRunning()) {
            ecosystem.simulateStep();
            updateUI();
        }
    }

    public void startAutoSimulation() {
        stepsRemaining = -1; // Infinito
        extinctionTarget = null; // Reset alvos
        if (!ecosystem.isLifeActive()) {
            JOptionPane.showMessageDialog(frame, "Reinicie o ecossistema primeiro.");
            return;
        }
        timer.start();
        updateUI();
    }

    public void stopSimulation() {
        timer.stop();
        updateUI();
    }

    public void runNSteps(int n) {
        if (n <= 0) return;
        stopSimulation();
        stepsRemaining = n;
        timer.start();
        updateUI();
    }

    public void runUntilExtinction(OrganismType target) {
        stopSimulation();
        this.extinctionTarget = target;
        timer.start();
        updateUI();
    }

    public void restartSimulation() {
        stopSimulation();
        ecosystem.restart();
        updateUI();
    }

    public void resizeGrid(int w, int h) {
        stopSimulation();
        initEcosystem(w, h);

        // Substituir o painel antigo pelo novo (necessário pois o tamanho mudou)
        frame.remove(simPanel);
        simPanel = new SimulationPanel(ecosystem);
        frame.add(simPanel, BorderLayout.CENTER);

        frame.revalidate();
        updateUI();
    }

    public void addOrganisms(OrganismType type, int qty) {
        for(int i=0; i<qty; i++) ecosystem.addOrganismRandomly(type);
        updateUI();
    }

    public void updateSpeed(int sps) {
        SimulationConfig.getInstance().setSTEPS_PER_SECOND(sps);
        int delay = 1000 / sps;
        timer.setDelay(delay);
    }

    public void openConfigDialog() {
        new ConfigDialog(frame).setVisible(true);
    }

    private void updateUI() {
        simPanel.repaint();
        int w = ecosystem.getOrganismCountByType(OrganismType.WOLF);
        int s = ecosystem.getOrganismCountByType(OrganismType.SHEEP);
        int p = ecosystem.getOrganismCountByType(OrganismType.PLANT);
        int h = ecosystem.getOrganismCountByType(OrganismType.HUNTER);
        controlPanel.updateStats(ecosystem.getStepCount(), w, s, p, h, timer.isRunning());
    }
}