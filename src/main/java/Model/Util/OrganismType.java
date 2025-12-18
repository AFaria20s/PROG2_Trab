package Model.Util;

import java.awt.Color;

public enum OrganismType {
    EMPTY(".", "Empty", new Color(255,255,255)),    // Branco
    HUNTER("X", "Hunter", new Color(255, 69, 0)),   // Laranja avermelhado
    PLANT("*", "Plant", new Color(34, 139, 34)),    // Verde Floresta
    SHEEP("O", "Sheep", new Color(100, 149, 237)),  // Azul
    WOLF("w", "Wolf", new Color(60, 60, 60));       // Cinzento Escuro

    private final String symbol;
    private final String type;
    private final Color color;

    OrganismType(String symbol, String type, Color color) {
        this.symbol = symbol;
        this.type = type;
        this.color = color;
    }

    public String getSymbol() { return symbol; }
    public String asString() { return type; }
    public Color getColor() { return color; }
}