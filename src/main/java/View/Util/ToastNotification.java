package View.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class ToastNotification extends JPanel {

    private final String message;
    private final Color backgroundColor;

    public ToastNotification(String message, Color color) {
        this.message = message;
        this.backgroundColor = color;
        setOpaque(false);
    }

    // --- Definir tamanho preferido para garantir que não é esmagado ---
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(220, 40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Anti-aliasing para ficar bonito (IA)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Desenha o fundo
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        // Desenha o texto
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));

        FontMetrics fm = g2.getFontMetrics();
        // Calculos para centrar texto (IA)
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() - 2;

        g2.drawString(message, x, y);
        g2.dispose();
    }

    public static void show(JFrame frame, String msg, Color color, int durationInMillis) {
        JRootPane root = frame.getRootPane();
        Component rawGlass = root.getGlassPane();
        JPanel glassPanel;

        // --- Garantir que o Layout é NULL ---
        if (rawGlass instanceof JPanel) {
            glassPanel = (JPanel) rawGlass;
            // Forçar layout nulo para o setBounds funcionar
            // Se nao vai haver conflitos com o tema FlatLaf que ja
            // tem configurado Toast
            glassPanel.setLayout(null);
        } else {
            glassPanel = new JPanel(null);
            root.setGlassPane(glassPanel);
        }

        glassPanel.setVisible(true);

        // Criar o Toast
        ToastNotification toast = new ToastNotification(msg, color);

        // Tamanho e Posição
        int toastWidth = 250; // Aumentei um pouco
        int toastHeight = 40;
        int margin = 20;

        // Definir coordenadas manuais
        int xPos = frame.getWidth() - toastWidth - margin;
        int yPos = margin;

        toast.setBounds(xPos, yPos, toastWidth, toastHeight);

        // Adiciona ao GlassPane
        glassPanel.add(toast);

        // Força a atualização visual imediata
        glassPanel.revalidate();
        glassPanel.repaint();

        // Timer para remover
        Timer timer = new Timer(durationInMillis, e -> {
            glassPanel.remove(toast);
            glassPanel.revalidate();
            glassPanel.repaint();

            if (glassPanel.getComponentCount() == 0) {
                glassPanel.setVisible(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}