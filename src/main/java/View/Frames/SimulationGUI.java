package View.Frames;

import Model.Ecosystem.Ecosystem;
import Model.Organisms.*;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;
import View.Dialogs.ConfigDialog;
import View.Panels.ControlPanel;
import View.Panels.SimulationPanel;
import View.ThemeType;
import View.Util.ToastNotification;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

/**
 * Controlador principal da interface gráfica da simulação.
 * Gere a janela principal (JFrame), o loop de simulação (Swing Timer) e a
 * resposta a eventos do utilizador (cliques de rato e teclas).
 * * <p>Implementa funcionalidades avançadas como:</p>
 * <ul>
 * <li><b>God Mode:</b> Edição direta da grelha através do rato.</li>
 * <li><b>Simulação Condicional:</b> Executar N passos ou até à extinção de uma espécie.</li>
 * <li><b>Notificações:</b> Avisos visuais via Toast quando eventos críticos ocorrem.</li>
 * </ul>
 */
public class SimulationGUI {
    private Ecosystem ecosystem;
    private JFrame frame;
    private SimulationPanel simPanel;
    private ControlPanel controlPanel;
    private Timer timer;

    private int stepsRemaining = -1; // -1 significa infinito
    private OrganismType extinctionTarget = null; // null significa qualquer extinção
    private int lastHunterCount;

    private boolean godModeActive = false;

    /**
     * Inicializa a interface, configura os Listeners de rato para o God Mode
     * e define os atalhos de teclado (ex: 'H' para spawnar caçador).
     */
    public SimulationGUI() {
        setupLookAndFeel();
        initEcosystem(SimulationConfig.getInstance().getWIDTH(), SimulationConfig.getInstance().getHEIGHT());
        setupFrame();

        lastHunterCount = 0;

        // Calculos feitos por (IA) devido ao offset que estava a dar problemas
        simPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (godModeActive) {
                    // Obter dimensões para recalcular o offset
                    int gridW = ecosystem.getWidth();
                    int gridH = ecosystem.getHeight();
                    int panelW = simPanel.getWidth();
                    int panelH = simPanel.getHeight();

                    int cellSize = calculateCellSize();

                    // Recalcular o offset (Exatamente como no SimulationPanel)
                    int offsetX = (panelW - (cellSize * gridW)) / 2;
                    int offsetY = (panelH - (cellSize * gridH)) / 2;

                    // Subtrair o offset da posição do clique
                    int clickX = e.getX() - offsetX;
                    int clickY = e.getY() - offsetY;

                    // Verificar se o clique foi dentro da área válida da grelha
                    if (clickX >= 0 && clickY >= 0) {
                        int x = clickX / cellSize;
                        int y = clickY / cellSize;

                        // Garante que não saiu dos limites da matriz (ex: margem direita/inferior)
                        if (x < gridW && y < gridH) {
                            Position pos = new Position(x, y);
                            Organism target = ecosystem.getOrganismAt(pos);

                            if (target instanceof Empty) {
                                switch (e.getButton()) {
                                    case 1:
                                        ecosystem.addOrganism(new Sheep(pos));
                                        break;
                                    case 2:
                                        ecosystem.addOrganism(new Plant(pos));
                                        break;
                                    case 3:
                                        ecosystem.addOrganism(new Wolf(pos));
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                ecosystem.removeOrganism(target);
                            }

                            updateUI();
                        }
                    }
                }
            }
        });

        // Caso o user clique H com godMode ativado, cria hunter
        InputMap inputMap = simPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = simPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "spawnHunter");
        actionMap.put("spawnHunter", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (godModeActive) addOrganisms(OrganismType.HUNTER, 1);
            }
        });
    }

    /**
     * Calcula o tamanho ideal de cada célula para que a grelha caiba perfeitamente
     * no espaço disponível do painel, mantendo a proporção quadrada.
     * @return O tamanho em píxeis do lado de cada célula.
     */
    public int calculateCellSize() {
        int gridW = ecosystem.getWidth();
        int gridH = ecosystem.getHeight();

        // Calcula quanto espaço cada quadrado pode ter na largura e na altura
        int cellW = simPanel.getWidth() / gridW;
        int cellH = simPanel.getHeight() / gridH;

        // Retornamos o menor dos dois para garantir que a célula é um quadrado perfeito
        // e que cabe dentro do painel.
        return Math.max(1, Math.min(cellW, cellH));
    }

    private void setupLookAndFeel() {
        try {
            ThemeType theme = SimulationConfig.getInstance().getTheme();

            switch (theme) {
                case LIGHT -> UIManager.setLookAndFeel(new FlatLightLaf());
                case DARK -> UIManager.setLookAndFeel(new FlatDarkLaf());
                case DARCULA -> UIManager.setLookAndFeel(new FlatDarculaLaf());
            }
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
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        updateUI();
    }

    // --- Simulacao ---

    /**
     * Ciclo principal de execução disparado pelo Timer.
     * Verifica condições de paragem (extinção ou limite de passos) e atualiza a UI.
     */
    private void executeStepLoop() {
        // Verifica se o ecossistema morreu
        if (!ecosystem.isLifeActive()) {
            stopTimerAndNotify("EXTINÇÃO TOTAL\nO ecossistema colapsou.");
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

        int currentHunters = ecosystem.getOrganismCountByType(OrganismType.HUNTER);

        // Se temos mais caçadores do que no passo anterior
        if (currentHunters > lastHunterCount) {
            ToastNotification.show(
                    frame,
                    "Caçador Apareceu!",
                    new Color(243, 118, 20, 220),
                    1500
            );
        }
        // Atualizamos a contagem para a próxima vez
        lastHunterCount = currentHunters;

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

    /**
     * Ativa o "God Mode", permitindo ao utilizador adicionar/remover organismos
     * clicando diretamente nas células da grelha.
     */
    public void enableGodMode() {
        this.godModeActive = true;
        // Podes mudar a cor da borda da grelha para avisar que está ativo
        simPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
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

    /**
     * Redimensiona a grelha da simulação. Para a execução atual e gera um novo ecossistema.
     * @param w Nova largura.
     * @param h Nova altura.
     */
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