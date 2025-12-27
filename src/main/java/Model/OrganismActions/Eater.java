package Model.OrganismActions;

import Model.Ecosystem.Ecosystem;

/**
 * Interface que define a capacidade de um organismo se alimentar.
 * Devem implementar esta interface todos os organismos que interagem com outros
 * para obter energia ou para os remover da simulação por via de predação/consumo.
 */
public interface Eater {
    /**
     * Executa a lógica de alimentação do organismo no contexto do ecossistema.
     * Geralmente envolve verificar a posição atual ou adjacente por presas válidas.
     * * @param eco O ecossistema onde a ação de comer ocorre.
     */
    void eat(Ecosystem eco);
}