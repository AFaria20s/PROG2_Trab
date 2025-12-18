package View.Panels;

import Model.Ecosystem.Ecosystem;
import Model.Organisms.*;
import Model.Util.OrganismType;
import Model.Util.Position;

import javax.swing.*;
import java.awt.*;

public class SimulationPanel extends JPanel {
    private final Ecosystem ecosystem;

    public SimulationPanel(Ecosystem ecosystem) {
        this.ecosystem = ecosystem;
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int gridW = ecosystem.getWidth();
        int gridH = ecosystem.getHeight();

        // Cálculo dinâmico para centralizar e manter quadrados perfeitos
        int panelW = getWidth();
        int panelH = getHeight();
        int cellSize = Math.min(panelW / gridW, panelH / gridH);

        // Evita células minúsculas ou erro de divisão por 0
        if (cellSize < 1) cellSize = 1;

        int offsetX = (panelW - (cellSize * gridW)) / 2;
        int offsetY = (panelH - (cellSize * gridH)) / 2;

        for (int y = 0; y < gridH; y++) {
            for (int x = 0; x < gridW; x++) {
                Organism org = ecosystem.getOrganismAt(new Position(x, y));

                if (org instanceof Wolf) g2d.setColor(Model.Util.OrganismType.WOLF.getColor());
                else if (org instanceof Sheep) g2d.setColor(Model.Util.OrganismType.SHEEP.getColor());
                else if (org instanceof Plant) g2d.setColor(Model.Util.OrganismType.PLANT.getColor());
                else if (org instanceof Hunter) g2d.setColor(OrganismType.HUNTER.getColor());
                else if (org instanceof Empty) g2d.setColor(OrganismType.EMPTY.getColor());

                int drawX = offsetX + (x * cellSize);
                int drawY = offsetY + (y * cellSize);

                g2d.fillRect(drawX, drawY, cellSize, cellSize);

                /**
                 * So desenha a borda da celula se ela for grande o suficiente
                 * para evitar erros visuais
                 */
                if (cellSize > 4) {
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.drawRect(drawX, drawY, cellSize, cellSize);
                }
            }
        }

        // Desenhar borda da grelha
        g2d.setColor(Color.GRAY);
        g2d.drawRect(offsetX, offsetY, gridW * cellSize, gridH * cellSize);
    }
}