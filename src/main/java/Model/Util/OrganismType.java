package Model.Util;

public enum OrganismType {
    PLANT('*'),
    SHEEP('O'),
    WOLF('w'),
    EMPTY('.')
    ;

    final char symbol;

    OrganismType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return this.symbol;
    }
}
