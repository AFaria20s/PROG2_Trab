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

    public void updateAge(Ecosystem eco) {
        this.age++;
        if (this.age > getMaxAgeLimit()) {
            eco.removeOrganism(this);
        }
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public boolean isAlive() { return alive; }
    public void die() { this.alive = false; }
    public OrganismType getType() { return type; }
    public String getDisplaySymbol() { return type.getSymbol(); }
}