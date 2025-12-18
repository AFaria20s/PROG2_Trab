package View;

public enum ThemeType {
    LIGHT("Flat Light"),
    DARK("Flat Dark"),
    DARCULA("Flat Darcula");

    private final String label;

    ThemeType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
