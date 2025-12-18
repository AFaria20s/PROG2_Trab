package View.Dialogs;

import Model.Util.SimulationConfig;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ConfigDialog extends JDialog {

    private final SimulationConfig config;

    public ConfigDialog(Frame owner) {
        super(owner, "Configurações do Ecossistema", true);
        this.config = SimulationConfig.getInstance();

        setLayout(new BorderLayout());
        // Reduzi um pouco a altura pois os componentes agora são mais compactos
        setSize(500, 550);
        setLocationRelativeTo(owner);
        setResizable(false); // Opcional: impede redimensionamento estranho

        JTabbedPane tabbedPane = new JTabbedPane();

        // Adicionando um pouco de padding interno às abas
        tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        tabbedPane.addTab("Geral", new JScrollPane(createSpawnPanel()));
        tabbedPane.addTab("Lobos", new JScrollPane(createWolfPanel()));
        tabbedPane.addTab("Ovelhas", new JScrollPane(createSheepPanel()));
        tabbedPane.addTab("Plantas", new JScrollPane(createPlantPanel()));
        tabbedPane.addTab("Caçador", new JScrollPane(createHunterPanel()));

        add(tabbedPane, BorderLayout.CENTER);

        // --- Botão Inferior ---
        JButton btnClose = new JButton("Aplicar & Fechar");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- PAINEL GERAL ---
    private JPanel createSpawnPanel() {
        JPanel main = createVerticalPanel();
        JPanel pSpawn = createGroupPanel("Probabilidade de Spawn Inicial");

        addProbSlider(pSpawn, "Lobo:", config.getPROB_WOLF_SPAWN(), 0, config::setPROB_WOLF_SPAWN);
        addProbSlider(pSpawn, "Ovelha:", config.getPROB_SHEEP_SPAWN(), 1, config::setPROB_SHEEP_SPAWN);
        addProbSlider(pSpawn, "Planta:", config.getPROB_PLANT_SPAWN(), 2, config::setPROB_PLANT_SPAWN);

        main.add(pSpawn);
        // Filler para empurrar tudo para cima
        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- PAINEL LOBOS ---
    private JPanel createWolfPanel() {
        JPanel main = createVerticalPanel();

        JPanel pLife = createGroupPanel("Ciclo de Vida & Energia");
        JSpinner sMaxAge = addIntSpinner(pLife, "Idade Máxima:", config.getWOLF_MAX_AGE(), 0);
        sMaxAge.addChangeListener(e -> config.setWOLF_MAX_AGE((Integer) sMaxAge.getValue()));

        JSpinner sEnergy = addIntSpinner(pLife, "Energia ao Comer:", config.getWOLF_ENERGY_GAIN_EAT(), 1);
        sEnergy.addChangeListener(e -> config.setWOLF_ENERGY_GAIN_EAT((Integer) sEnergy.getValue()));
        main.add(pLife);

        JPanel pBehave = createGroupPanel("Probabilidades");
        addProbSlider(pBehave, "Reprodução:", config.getWOLF_REPRODUCTION_PROB(), 0, config::setWOLF_REPRODUCTION_PROB);
        addProbSlider(pBehave, "Sucesso Ataque:", config.getWOLF_EAT_PROB(), 1, config::setWOLF_EAT_PROB);
        main.add(pBehave);

        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- PAINEL OVELHAS ---
    private JPanel createSheepPanel() {
        JPanel main = createVerticalPanel();

        JPanel pLife = createGroupPanel("Ciclo de Vida & Energia");
        JSpinner sMaxAge = addIntSpinner(pLife, "Idade Máxima:", config.getSHEEP_MAX_AGE(), 0);
        sMaxAge.addChangeListener(e -> config.setSHEEP_MAX_AGE((Integer) sMaxAge.getValue()));
        main.add(pLife);

        JPanel pBehave = createGroupPanel("Probabilidades");
        addProbSlider(pBehave, "Reprodução:", config.getSHEEP_REPRODUCTION_PROB(), 0, config::setSHEEP_REPRODUCTION_PROB);
        addProbSlider(pBehave, "Vontade Comer:", config.getSHEEP_EAT_PROB(), 1, config::setSHEEP_EAT_PROB);
        main.add(pBehave);

        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- PAINEL PLANTAS ---
    private JPanel createPlantPanel() {
        JPanel main = createVerticalPanel();
        JPanel pGen = createGroupPanel("Geral");

        addProbSlider(pGen, "Crescimento:", config.getPLANT_REPRODUCTION_PROB(), 0, config::setPLANT_REPRODUCTION_PROB);
        JSpinner sAge = addIntSpinner(pGen, "Idade Máxima:", config.getPLANT_MAX_AGE(), 1);
        sAge.addChangeListener(e -> config.setPLANT_MAX_AGE((Integer) sAge.getValue()));

        main.add(pGen);
        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- PAINEL CAÇADOR ---
    private JPanel createHunterPanel() {
        JPanel main = createVerticalPanel();
        JPanel pProb = createGroupPanel("Probabilidades");

        addProbSlider(pProb, "Aparecer:", config.getPROB_HUNTER_APPEARANCE(), 0, config::setPROB_HUNTER_APPEARANCE);
        addProbSlider(pProb, "Precisão Tiro:", config.getHUNTER_BASE_HUNT_PROB(), 1, config::setHUNTER_BASE_HUNT_PROB);
        main.add(pProb);

        JPanel pLim = createGroupPanel("Limites");
        JSpinner sRadius = addIntSpinner(pLim, "Raio Visão:", config.getHUNTER_HUNT_RADIUS(), 0);
        sRadius.addChangeListener(e -> config.setHUNTER_HUNT_RADIUS((Integer) sRadius.getValue()));
        main.add(pLim);

        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- HELPERS PARA INTERFACE ---

    /**
     * Adiciona um Slider usando GridBagLayout.
     */
    private void addProbSlider(JPanel p, String labelText, double initial, int gridY, java.util.function.DoubleConsumer setter) {
        JLabel label = new JLabel(labelText);

        // Contentor para Slider + Label de %
        JPanel sliderCont = new JPanel(new BorderLayout(5, 0));
        int initialInt = (int) (initial * 100);

        JSlider slider = new JSlider(0, 100, initialInt);
        slider.setFocusable(false);
        // Opcional: Adicionar ticks para ficar mais bonito
        // slider.setMajorTickSpacing(25);
        // slider.setPaintTicks(true);

        JLabel valLabel = new JLabel(String.format("%3d%%", initialInt));
        valLabel.setPreferredSize(new Dimension(45, 20));
        valLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        slider.addChangeListener(e -> {
            int val = slider.getValue();
            valLabel.setText(String.format("%3d%%", val));
            setter.accept(val / 100.0);
        });

        sliderCont.add(slider, BorderLayout.CENTER);
        sliderCont.add(valLabel, BorderLayout.EAST);

        // Adiciona ao GridBagLayout do painel pai
        addToGrid(p, label, sliderCont, gridY);
    }

    private JSpinner addIntSpinner(JPanel p, String labelText, int initial, int gridY) {
        JLabel label = new JLabel(labelText);
        SpinnerNumberModel model = new SpinnerNumberModel(initial, 0, 10000, 1);
        JSpinner spinner = new JSpinner(model);

        // Formata o spinner para um tamanho fixo razoável
        JComponent editor = spinner.getEditor();
        JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setColumns(4); // Define largura para ~4 dígitos

        // Envolve num FlowLayout LEFT para não esticar horizontalmente
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.add(spinner);

        addToGrid(p, label, wrapper, gridY);
        return spinner;
    }

    /**
     * Método auxiliar para adicionar Label + Componente ao GridBagLayout
     */
    private void addToGrid(JPanel p, JLabel label, JComponent comp, int gridY) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margem entre componentes
        gbc.anchor = GridBagConstraints.WEST; // Alinha à esquerda

        // Configuração da Label (Coluna 0)
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.weightx = 0.0; // Não cresce
        gbc.fill = GridBagConstraints.NONE;
        p.add(label, gbc);

        // Configuração do Input (Coluna 1)
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Cresce para ocupar o resto da linha
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(comp, gbc);
    }

    private JPanel createVerticalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return p;
    }

    private JPanel createGroupPanel(String title) {
        // MUDANÇA IMPORTANTE: GridBagLayout em vez de GridLayout
        JPanel p = new JPanel(new GridBagLayout());

        p.setBorder(new CompoundBorder(
                new TitledBorder(
                        BorderFactory.createEtchedBorder(), title,
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 12)
                ),
                new EmptyBorder(5, 5, 5, 5) // Padding interno dentro da borda
        ));

        // Força o painel a ter tamanho máximo vertical para não esticar feio no BoxLayout
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }
}