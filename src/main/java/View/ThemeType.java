package View;

/**
 * Enumeração dos temas visuais suportados pela aplicação.
 * Integra-se com a biblioteca FlatLaf para fornecer aspetos visuais modernos.
 */
public enum ThemeType {
    /** Tema claro padrão. */
    LIGHT("Flat Light"),
    /** Tema escuro genérico. */
    DARK("Flat Dark"),
    /** Tema escuro estilo IntelliJ IDEA. */
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
