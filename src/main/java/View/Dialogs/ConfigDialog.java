package View.Dialogs;

import Model.Util.SimulationConfig;
import View.ThemeType;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
/**
 * Janela de diálogo modal para configuração de parâmetros da simulação.
 * Organiza as configurações em separadores (Tabs) para facilitar o ajuste de:
 * <ul>
 * <li>Probabilidades de spawn inicial e Temas visuais.</li>
 * <li>Metabolismo e reprodução de Lobos e Ovelhas.</li>
 * <li>Ciclo de vida das Plantas.</li>
 * <li>Comportamento e limites de energia do Caçador.</li>
 * </ul>
 */
public class ConfigDialog extends JDialog {

    private final SimulationConfig config;

    /**
     * Constrói o diálogo de configuração.
     * @param owner A janela principal (Frame) à qual este diálogo pertence.
     */
    public ConfigDialog(Frame owner) {
        super(owner, "Configurações do Ecossistema", true);
        this.config = SimulationConfig.getInstance();

        setLayout(new BorderLayout());
        setSize(500, 600); // Aumentei ligeiramente a altura para caber os novos inputs
        setLocationRelativeTo(owner);
        setResizable(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        tabbedPane.addTab("Geral", new JScrollPane(createSpawnPanel()));
        tabbedPane.addTab("Lobos", new JScrollPane(createWolfPanel()));
        tabbedPane.addTab("Ovelhas", new JScrollPane(createSheepPanel()));
        tabbedPane.addTab("Plantas", new JScrollPane(createPlantPanel()));
        tabbedPane.addTab("Caçador", new JScrollPane(createHunterPanel()));

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Aplicar & Fechar");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- PAINEL GERAL (Mantido igual) ---
    private JPanel createSpawnPanel() {
        JPanel main = createVerticalPanel();
        JPanel pSpawn = createGroupPanel("Probabilidade de Spawn Inicial");

        addProbSlider(pSpawn, "Lobo:", config.getPROB_WOLF_SPAWN(), 0, config::setPROB_WOLF_SPAWN);
        addProbSlider(pSpawn, "Ovelha:", config.getPROB_SHEEP_SPAWN(), 1, config::setPROB_SHEEP_SPAWN);
        addProbSlider(pSpawn, "Planta:", config.getPROB_PLANT_SPAWN(), 2, config::setPROB_PLANT_SPAWN);

        main.add(pSpawn);
        main.add(Box.createVerticalGlue());

        JPanel pUI = createGroupPanel("Aparência");

        JLabel lblTheme = new JLabel("Tema:");
        JComboBox<ThemeType> cbTheme = new JComboBox<>(ThemeType.values());
        cbTheme.setSelectedItem(config.getTheme());

        cbTheme.addActionListener(e -> {
            ThemeType selected = (ThemeType) cbTheme.getSelectedItem();
            if (selected != null) {
                config.setTheme(selected);
                applyTheme(selected);
            }
        });

        addToGrid(pUI, lblTheme, cbTheme, 0);
        main.add(pUI);

        return main;
    }
    /**
     * Aplica o LookAndFeel selecionado a todos os componentes da aplicação.
     * @param theme O tema visual (LIGHT, DARK, DARCULA).
     */
    private void applyTheme(ThemeType theme) {
        try {
            switch (theme) {
                case LIGHT -> UIManager.setLookAndFeel(new FlatLightLaf());
                case DARK -> UIManager.setLookAndFeel(new FlatDarkLaf());
                case DARCULA -> UIManager.setLookAndFeel(new FlatDarculaLaf());
            }

            // Atualiza TODAS as janelas abertas
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
                w.pack();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // --- PAINEL LOBOS (Atualizado com Metabolismo) ---
    private JPanel createWolfPanel() {
        JPanel main = createVerticalPanel();

        JPanel pLife = createGroupPanel("Energia & Metabolismo");
        // Linha 0
        JSpinner sMaxAge = addIntSpinner(pLife, "Idade Máxima:", config.getWOLF_MAX_AGE(), 0);
        sMaxAge.addChangeListener(e -> config.setWOLF_MAX_AGE((Integer) sMaxAge.getValue()));

        // Linha 1
        JSpinner sEnergy = addIntSpinner(pLife, "Ganho ao Comer:", config.getWOLF_ENERGY_GAIN_EAT(), 1);
        sEnergy.addChangeListener(e -> config.setWOLF_ENERGY_GAIN_EAT((Integer) sEnergy.getValue()));

        // Linha 2 - NOVO
        JSpinner sCostStep = addIntSpinner(pLife, "Custo p/ Passo:", config.getWOLF_ENERGY_COST_STEP(), 2);
        sCostStep.addChangeListener(e -> config.setWOLF_ENERGY_COST_STEP((Integer) sCostStep.getValue()));

        // Linha 3 - NOVO
        JSpinner sCostRep = addIntSpinner(pLife, "Custo Reprodução:", config.getWOLF_REPRODUCTION_COST(), 3);
        sCostRep.addChangeListener(e -> config.setWOLF_REPRODUCTION_COST((Integer) sCostRep.getValue()));

        main.add(pLife);

        JPanel pBehave = createGroupPanel("Probabilidades");
        addProbSlider(pBehave, "Reprodução:", config.getWOLF_REPRODUCTION_PROB(), 0, config::setWOLF_REPRODUCTION_PROB);
        addProbSlider(pBehave, "Sucesso Ataque:", config.getWOLF_EAT_PROB(), 1, config::setWOLF_EAT_PROB);
        main.add(pBehave);

        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- PAINEL OVELHAS (Atualizado com Metabolismo) ---
    private JPanel createSheepPanel() {
        JPanel main = createVerticalPanel();

        JPanel pLife = createGroupPanel("Energia & Metabolismo");

        JSpinner sMaxAge = addIntSpinner(pLife, "Idade Máxima:", config.getSHEEP_MAX_AGE(), 0);
        sMaxAge.addChangeListener(e -> config.setSHEEP_MAX_AGE((Integer) sMaxAge.getValue()));

        JSpinner sGain = addIntSpinner(pLife, "Ganho ao Comer:", config.getSHEEP_ENERGY_GAIN_EAT(), 1);
        sGain.addChangeListener(e -> config.setSHEEP_ENERGY_GAIN_EAT((Integer) sGain.getValue()));

        // NOVO: Custo de vida da ovelha
        JSpinner sCostStep = addIntSpinner(pLife, "Custo p/ Passo:", config.getSHEEP_ENERGY_COST_STEP(), 2);
        sCostStep.addChangeListener(e -> config.setSHEEP_ENERGY_COST_STEP((Integer) sCostStep.getValue()));

        // NOVO: Custo para ter filhos
        JSpinner sCostRep = addIntSpinner(pLife, "Custo Reprodução:", config.getSHEEP_REPRODUCTION_COST(), 3);
        sCostRep.addChangeListener(e -> config.setSHEEP_REPRODUCTION_COST((Integer) sCostRep.getValue()));

        main.add(pLife);

        JPanel pBehave = createGroupPanel("Probabilidades");
        addProbSlider(pBehave, "Reprodução:", config.getSHEEP_REPRODUCTION_PROB(), 0, config::setSHEEP_REPRODUCTION_PROB);
        addProbSlider(pBehave, "Vontade Comer:", config.getSHEEP_EAT_PROB(), 1, config::setSHEEP_EAT_PROB);
        main.add(pBehave);

        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- PAINEL PLANTAS (Mantido igual) ---
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

    // --- PAINEL CAÇADOR (Atualizado com Limites de Energia) ---
    private JPanel createHunterPanel() {
        JPanel main = createVerticalPanel();
        JPanel pProb = createGroupPanel("Comportamento");

        addProbSlider(pProb, "Aparecer:", config.getPROB_HUNTER_APPEARANCE(), 0, config::setPROB_HUNTER_APPEARANCE);
        addProbSlider(pProb, "Precisão Tiro:", config.getHUNTER_BASE_HUNT_PROB(), 1, config::setHUNTER_BASE_HUNT_PROB);
        main.add(pProb);

        JPanel pLim = createGroupPanel("Limites & Energia");
        JSpinner sRadius = addIntSpinner(pLim, "Raio Visão:", config.getHUNTER_HUNT_RADIUS(), 0);
        sRadius.addChangeListener(e -> config.setHUNTER_HUNT_RADIUS((Integer) sRadius.getValue()));

        // NOVO: Energia inicial/máxima do caçador
        JSpinner sMaxEnergy = addIntSpinner(pLim, "Energia Inicial:", config.getHUNTER_MAX_ENERGY(), 1);
        sMaxEnergy.addChangeListener(e -> config.setHUNTER_MAX_ENERGY((Integer) sMaxEnergy.getValue()));

        // NOVO: Quanto ele gasta por andar
        JSpinner sCost = addIntSpinner(pLim, "Custo p/ Passo:", config.getHUNTER_ENERGY_COST_STEP(), 2);
        sCost.addChangeListener(e -> config.setHUNTER_ENERGY_COST_STEP((Integer) sCost.getValue()));

        main.add(pLim);

        main.add(Box.createVerticalGlue());
        return main;
    }

    // --- MÉTODOS AUXILIARES (IGUAIS AO ANTERIOR) ---
    // Copia e cola os métodos addProbSlider, addIntSpinner, addToGrid, createVerticalPanel, createGroupPanel
    // do código anterior, pois não precisam de mudar.

    /**
     * Cria um painel com um slider configurado para manipular valores decimais (0.0 a 1.0).
     * @param p O painel de destino.
     * @param labelText Texto descritivo.
     * @param initial Valor inicial.
     * @param gridY Posição na grelha vertical.
     * @param setter Método de callback para atualizar o SimulationConfig.
     */
    private void addProbSlider(JPanel p, String labelText, double initial, int gridY, java.util.function.DoubleConsumer setter) {
        JLabel label = new JLabel(labelText);
        JPanel sliderCont = new JPanel(new BorderLayout(5, 0));
        int initialInt = (int) (initial * 100);
        JSlider slider = new JSlider(0, 100, initialInt);
        slider.setFocusable(false);
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
        addToGrid(p, label, sliderCont, gridY);
    }

    private JSpinner addIntSpinner(JPanel p, String labelText, int initial, int gridY) {
        JLabel label = new JLabel(labelText);
        SpinnerNumberModel model = new SpinnerNumberModel(initial, 0, 10000, 1);
        JSpinner spinner = new JSpinner(model);
        JComponent editor = spinner.getEditor();
        JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setColumns(4);
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.add(spinner);
        addToGrid(p, label, wrapper, gridY);
        return spinner;
    }

    private void addToGrid(JPanel p, JLabel label, JComponent comp, int gridY) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = gridY;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        p.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
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
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createEtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)),
                new EmptyBorder(5, 5, 5, 5)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }
}