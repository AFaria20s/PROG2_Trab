package Model.Util;

public enum OrganismType {
    PLANT('*', "Plant"),
    SHEEP('O', "Sheep"),
    WOLF('w', "Wolf"),
    EMPTY('.', "Empty")
    ;

    final char symbol;
    final String type;

    OrganismType(char symbol, String type) {
        this.symbol = symbol;
        this.type = type;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public String asString() {
        return this.type;
    }
}
