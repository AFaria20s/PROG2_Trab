package Model.OrganismActions;

import Model.Ecosystem.Ecosystem;

/**
 * Interface que define a capacidade de locomoção de um organismo.
 * É utilizada por entidades que podem alterar a sua posição na grelha do ecossistema
 * a cada passo da simulação.
 */
public interface Movable {
    /**
     * Executa o deslocamento do organismo para uma nova posição.
     * A implementação deve validar se o movimento é possível e atualizar a
     * grelha do ecossistema.
     * * @param eco O ecossistema onde o movimento será processado.
     */
    void move(Ecosystem eco);
}