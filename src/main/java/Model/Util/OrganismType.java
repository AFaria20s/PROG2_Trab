package Model.Util;

public enum OrganismType {
    HUNTER("X", "Hunter"),
    PLANT("*", "Plant"),
    SHEEP("O", "Sheep"),
    WOLF("w", "Wolf"),
    EMPTY(".", "Empty")
    ;

    final String symbol;
    final String type;

    OrganismType(String symbol, String type) {
        this.symbol = symbol;
        this.type = type;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String asString() {
        return this.type;
    }
}
