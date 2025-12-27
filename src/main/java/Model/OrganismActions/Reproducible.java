package Model.OrganismActions;

import Model.Ecosystem.Ecosystem;

/**
 * Interface que define a capacidade de um organismo gerar descendência.
 * Pode ser implementada tanto para reprodução sexuada (requer parceiro)
 * como assexuada (propagação automática).
 */
public interface Reproducible {
    /**
     * Executa a lógica de reprodução do organismo.
     * Se as condições (energia, idade, espaço ou parceiro) forem cumpridas,
     * um novo organismo deve ser adicionado ao ecossistema.
     * * @param eco O ecossistema onde o novo organismo será gerado.
     */
    void reproduce(Ecosystem eco);
}