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
        this.type = type;
        this.age = 0;
        this.alive = true;
    }

    public abstract void step(Ecosystem eco);
    protected abstract int getMaxAgeLimit();

    /**
     * Retorna o símbolo visual. Classes com energia podem sobrescrever isto.
     */
    public String getDisplaySymbol() {
        return type.getSymbol();
    }

    public void increaseAge() { this.age++; }

    public void checkMaxAge() {
        if (this.age > getMaxAgeLimit()) die();
    }

    public void die() { this.alive = false; }
    public boolean isAlive() { return alive; }
    public OrganismType getType() { return type; }
    public Position getPosition() { return position; }
    public void setPosition(Position pos) { this.position = pos; }
    public int getAge() { return age; }
}