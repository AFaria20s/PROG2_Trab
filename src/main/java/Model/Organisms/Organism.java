package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.Position;

/**
 * Classe abstrata que serve de base para todas as entidades do ecossistema.
 * Define propriedades fundamentais como posição, idade e estado vital.
 */
public abstract class Organism {
    private Position position;
    private int age;
    private boolean alive;
    private final OrganismType type;

    /**
     * Construtor base para um organismo.
     * @param pos Posição inicial na grelha.
     * @param type Tipo de organismo (conforme definido no Enum OrganismType).
     */
    public Organism(Position pos, OrganismType type) {
        this.position = pos;
        this.type = type;
        this.age = 1;
        this.alive = true;
    }

    /**
     * Define as ações que o organismo executa em cada passo da simulação.
     * @param eco Referência ao ecossistema para interagir com o ambiente.
     */
    public abstract void step(Ecosystem eco);

    /** @return O limite máximo de idade definido para esta espécie. */
    protected abstract int getMaxAgeLimit();

    /**
     * Incrementa a idade do organismo e verifica se este deve morrer por velhice.
     * @param eco Ecossistema de onde o organismo será removido em caso de morte.
     */
    public void updateAge(Ecosystem eco) {
        this.age++;
        if (this.age > getMaxAgeLimit()) {
            eco.removeOrganism(this);
        }
    }

    // Getters e Setters
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public boolean isAlive() { return alive; }
    public void die() { this.alive = false; }
    public OrganismType getType() { return type; }
    public String getDisplaySymbol() { return type.getSymbol(); }
}