package Model.Util;

/**
 * Representa um par de coordenadas (x, y) na grelha bidimensional do ecossistema.
 * É utilizada para localizar organismos e calcular adjacências.
 */
public class Position {
    private int x;
    private int y;

    /**
     * Cria uma nova posição com as coordenadas especificadas.
     * @param x Coordenada horizontal (coluna).
     * @param y Coordenada vertical (linha).
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** @return O valor da coordenada X. */
    public int getX() { return x; }

    /** @return O valor da coordenada Y. */
    public int getY() { return y; }
}