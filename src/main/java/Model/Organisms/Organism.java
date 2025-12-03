package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.Position;

public abstract class Organism {
    private Position position;
    private int age;
    private boolean alive;
    private final OrganismType type;

    public Organism(Position pos, OrganismType type) {
        this.position = pos;
        this.age = 0;
        this.alive = true;
        this.type = type;
    }

    /**
     * Define o comportamento completo do organismo num passo da simulação.
     * Deve ser implementado por cada espécie.
     */
    public abstract void step(Ecosystem eco);
    protected abstract int getMaxAgeLimit();

    public void increaseAge() {
        this.age++;
    }

    public void checkMaxAge() {
        if (this.age > getMaxAgeLimit()) {
            this.die();
        }
    }

    public void die() {
        this.alive = false;
    }

    // Getters e Setters
    public boolean isAlive() {
        return alive;
    }

    public OrganismType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }
}
