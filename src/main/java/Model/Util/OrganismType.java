package Model.Util;

import java.awt.Color;

/**
 * Define as categorias de organismos existentes no ecossistema.
 * Associa a cada tipo um símbolo textual, um nome descritivo e uma cor para a 
 * representação gráfica na interface Swing.
 */
public enum OrganismType {
    /** Célula vazia ou sem vida. */
    EMPTY(".", "Empty", new Color(252, 252, 252)),
    /** Predador de topo controlado por regras especiais. */
    HUNTER("X", "Hunter", new Color(255, 69, 0)),
    /** Organismo produtor (base da cadeia alimentar). */
    PLANT("*", "Plant", new Color(103, 196, 103)),
    /** Animal herbívoro (presa). */
    SHEEP("O", "Sheep", new Color(69, 113, 192)),
    /** Animal carnívoro (predador). */
    WOLF("w", "Wolf", new Color(82, 57, 57)),

    BEAR("b", "Bear", new Color(226, 35, 248));

    private final String symbol;
    private final String type;
    private final Color color;

    OrganismType(String symbol, String type, Color color) {
        this.symbol = symbol;
        this.type = type;
        this.color = color;
    }

    /** @return O carácter que representa o organismo na consola. */
    public String getSymbol() { return symbol; }

    /** @return O nome do tipo em formato String. */
    public String asString() { return type; }

    /** @return A cor associada para renderização no SimulationPanel. */
    public Color getColor() { return color; }
}