package Model.Util;

import java.util.Random;

/**
 * Define as direções cardeais possíveis para a movimentação dos organismos na grelha.
 * Cada direção contém os vetores de deslocamento (dx, dy) necessários para calcular
 * a nova posição num plano 2D.
 */
public enum Direction {
    /** Movimento para cima (diminui o índice da linha). */
    NORTH(0, -1),
    /** Movimento para baixo (aumenta o índice da linha). */
    SOUTH(0, 1),
    /** Movimento para a direita (aumenta o índice da coluna). */
    EAST(1, 0),
    /** Movimento para a esquerda (diminui o índice da coluna). */
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** @return O deslocamento no eixo X (colunas). */
    public int getDx() { return dx; }

    /** @return O deslocamento no eixo Y (linhas). */
    public int getDy() { return dy; }

    /**
     * Seleciona uma direção aleatória entre as disponíveis.
     * @return Uma instância de Direction escolhida ao acaso.
     */
    public static Direction getRandomDirection() {
        Direction[] values = Direction.values();
        int index = new Random().nextInt(values.length);
        return values[index];
    }
}