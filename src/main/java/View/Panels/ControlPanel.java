package View.Panels;

import Model.Util.OrganismType;
import Model.Util.SimulationConfig;
import View.Frames.SimulationGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanel extends JPanel {
    private final SimulationGUI mainGUI;

    // UI Elements
    private JLabel lblStep, lblWolf, lblSheep, lblPlant, lblHunter, lblStatus; // Adicionado lblHunter
    private JButton btnStartStop;
    private JSlider speedSlider;

    // Easter... what...? egg!
    private JButton btnGodMode;
    private int secretClickCount = 0;
    private long lastClickTime = 0;

    public ControlPanel(SimulationGUI gui) {
        this.mainGUI = gui;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- SECÇÃO 1: LEGENDA (Cores) ---
        addHeader("Legenda");
        // Cria os itens da legenda dinamicamente usando as cores do Enum
        addLegendItem("Lobo", OrganismType.WOLF.getColor());
        addLegendItem("Ovelha", OrganismType.SHEEP.getColor());
        addLegendItem("Planta", OrganismType.PLANT.getColor());
        addLegendItem("Caçador", OrganismType.HUNTER.getColor());

        addSpacer();

        // --- SECÇÃO 2: ESTATÍSTICAS ---
        addHeader("Estatísticas");
        lblStatus = addLabel("Status: Parado", new Font("SansSerif", Font.BOLD, 12));
        lblStatus.setForeground(Color.RED);
        add(Box.createRigidArea(new Dimension(0, 5)));

        // Inicialização dos Labels
        lblStep = addLabel("Steps: 0", null);
        lblWolf = addLabel("Lobos: 0", null);
        lblSheep = addLabel("Ovelhas: 0", null);
        lblPlant = addLabel("Plantas: 0", null);
        lblHunter = addLabel("Caçadores: 0", null); // Novo Label

        addSpacer();

        // --- SECÇÃO 3: CONTROLO DE TEMPO ---
        addHeader("Controlo Execução");

        // EASSTERREEGGG
        btnGodMode = new JButton("GOD MODE");
        btnGodMode.setVisible(false); // Começa escondido
        btnGodMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Mais alto que os outros
        btnGodMode.setBackground(new Color(255, 215, 0)); // Dourado
        btnGodMode.setForeground(Color.BLACK);
        btnGodMode.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnGodMode.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ação do botão
        btnGodMode.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "MODO DEUS ATIVADO!\nAgora podes clicar na grelha para criar/remover vida.");
            mainGUI.enableGodMode();
        });

        // --- DETETOR DE CLIQUES ---
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastClickTime < 1000) {
                    secretClickCount++;
                } else {
                    secretClickCount = 1;
                }

                lastClickTime = currentTime;

                if (secretClickCount >= 5) {
                    btnGodMode.setVisible(true);
                    revalidate();
                    repaint();
                    secretClickCount = 0;
                }
            }
        });

        // Adiciona-o ao painel (por exemplo, abaixo do Iniciar/Pausar)
        add(btnGodMode);
        add(Box.createRigidArea(new Dimension(0, 10)));

        btnStartStop = addButton("Iniciar", e -> togglePlayPause());
        addButton("Passo Unico", e -> mainGUI.runOneStep()); // Assumi que runOneStep = runStep
        addButton("Correr N Passos", e -> askNSteps());
        addButton("Correr até Extinção", e -> askExtinctionTarget());

        add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lSpeed = new JLabel("Velocidade - "+SimulationConfig.getInstance().getSTEPS_PER_SECOND()+" SPS");
        lSpeed.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lSpeed);

        speedSlider = new JSlider(1, 40, SimulationConfig.getInstance().getSTEPS_PER_SECOND());
        speedSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        speedSlider.setOpaque(false);
        speedSlider.addChangeListener(e -> {
            if(!speedSlider.getValueIsAdjusting()) mainGUI.updateSpeed(speedSlider.getValue());
            lSpeed.setText("Velocidade - "+SimulationConfig.getInstance().getSTEPS_PER_SECOND()+" SPS");
        });
        add(speedSlider);

        addSpacer();

        // --- SECÇÃO 4: ECOSSISTEMA ---
        addHeader("Ecossistema");
        addButton("Adicionar Organismos", e -> showAddOrganismDialog());
        addButton("Reiniciar (Reset)", e -> mainGUI.restartSimulation());
        addButton("Redimensionar Grelha", e -> showResizeDialog());

        addSpacer();

        // --- SECÇÃO 5: SISTEMA ---
        addHeader("Sistema");
        addButton("Configurações Avançadas", e -> mainGUI.openConfigDialog());
        addButton("Sair", e -> System.exit(0));
    }

    // --- Helpers Visuais (Legenda) ---

    private void addLegendItem(String name, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // O quadrado colorido
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15)); // Tamanho do quadrado
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Borda fina

        JLabel label = new JLabel(name);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(label);
        add(panel);
    }

    // --- Actions Helpers ---

    private void togglePlayPause() {
        if (btnStartStop.getText().startsWith("Pausar")) {
            mainGUI.stopSimulation();
        } else {
            mainGUI.startAutoSimulation();
        }
    }

    private void askNSteps() {
        String input = JOptionPane.showInputDialog(this, "Quantos passos deseja executar?");
        if (input != null) {
            try {
                int n = Integer.parseInt(input);
                mainGUI.runNSteps(n);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido.");
            }
        }
    }

    private void askExtinctionTarget() {
        String[] options = {"Total", "Lobo", "Ovelha", "Planta"};
        int choice = JOptionPane.showOptionDialog(this, "Parar quando houver extinção de:", "Modo Extinção",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) mainGUI.startAutoSimulation();
        else if (choice == 1) mainGUI.runUntilExtinction(OrganismType.WOLF);
        else if (choice == 2) mainGUI.runUntilExtinction(OrganismType.SHEEP);
        else if (choice == 3) mainGUI.runUntilExtinction(OrganismType.PLANT);
    }

    private void showAddOrganismDialog() {
        String[] options = {"Lobo", "Ovelha", "Planta"};
        int typeIdx = JOptionPane.showOptionDialog(this, "Organismo:", "Adicionar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (typeIdx >= 0) {
            String qtyStr = JOptionPane.showInputDialog(this, "Quantidade:", "5");
            try {
                int qty = Integer.parseInt(qtyStr);
                OrganismType type = switch (typeIdx) {
                    case 0 -> OrganismType.WOLF;
                    case 1 -> OrganismType.SHEEP;
                    case 2 -> OrganismType.PLANT;
                    default -> null;
                };
                if (type != null) mainGUI.addOrganisms(type, qty);
            } catch (Exception ignored) {}
        }
    }

    private void showResizeDialog() {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField wField = new JTextField(String.valueOf(SimulationConfig.getInstance().getWIDTH()));
        JTextField hField = new JTextField(String.valueOf(SimulationConfig.getInstance().getHEIGHT()));
        p.add(new JLabel("Largura:")); p.add(wField);
        p.add(new JLabel("Altura:")); p.add(hField);

        int res = JOptionPane.showConfirmDialog(this, p, "Novo Tamanho", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                mainGUI.resizeGrid(Integer.parseInt(wField.getText()), Integer.parseInt(hField.getText()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Valores inválidos.");
            }
        }
    }

    // --- UI Helpers ---

    private void addHeader(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(l);
        add(Box.createRigidArea(new Dimension(0, 5)));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        add(sep);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addSpacer() {
        add(Box.createRigidArea(new Dimension(0, 20))); // Reduzi um pouco o espaço para caber a legenda
    }

    private JLabel addLabel(String text, Font font) {
        JLabel l = new JLabel(text);
        if (font != null) l.setFont(font);
        else l.setFont(new Font("Monospaced", Font.PLAIN, 14));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(l);
        return l;
    }

    private JButton addButton(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        b.addActionListener(action);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(b);
        add(Box.createRigidArea(new Dimension(0, 5)));
        return b;
    }

    // --- Update Method CORRIGIDO ---
    public void updateStats(int step, int w, int s, int p, int h, boolean isRunning) {
        lblStep.setText(String.format("Steps:   %d", step));
        lblWolf.setText(String.format("Lobos:   %d", w));
        lblSheep.setText(String.format("Ovelhas: %d", s));

        // CORREÇÃO AQUI: Cada um tem o seu label
        lblPlant.setText(String.format("Plantas: %d", p));
        lblHunter.setText(String.format("Caçadores: %d", h));

        if (isRunning) {
            btnStartStop.setText("Pausar");
            btnStartStop.setBackground(new Color(255, 200, 200));
            lblStatus.setText("Status: A CORRER");
            lblStatus.setForeground(new Color(0, 150, 0));
        } else {
            btnStartStop.setText("Iniciar");
            btnStartStop.setBackground(UIManager.getColor("Button.background"));
            lblStatus.setText("Status: PARADO");
            lblStatus.setForeground(Color.RED);
        }
    }
}