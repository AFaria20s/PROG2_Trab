package Model.Util;

import java.util.Random;

public enum Direction {
    // Definir as direções e os seus deslocamentos (dx, dy)
    NORTH(0, -1),   // X não muda, Y diminui
    SOUTH(0, 1),    // X não muda, Y aumenta
    EAST(1, 0),     // X aumenta, Y não muda
    WEST(-1, 0);    // X diminui, Y não muda

    private final int dx; // mudança no eixo X
    private final int dy; // mudança no eixo Y

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    /**
     * Obter diracao aleatoria
     */
    public static Direction getRandomDirection() {
        Direction[] values = Direction.values();
        // Escolhe um índice aleatório
        int index = new Random().nextInt(values.length);
        return values[index];
    }
}