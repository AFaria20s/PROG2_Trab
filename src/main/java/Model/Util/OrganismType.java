package Model.Util;

import java.awt.Color;

public enum OrganismType {
    EMPTY(".", "Empty", new Color(252, 252, 252)),
    HUNTER("X", "Hunter", new Color(255, 69, 0)),
    PLANT("*", "Plant", new Color(103, 196, 103)),
    SHEEP("O", "Sheep", new Color(69, 113, 192)),
    WOLF("w", "Wolf", new Color(82, 57, 57))
    ;

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