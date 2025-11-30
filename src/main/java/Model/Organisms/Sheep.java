package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Eater;
import Model.OrganismActions.Movable;
import Model.OrganismActions.Reproducible;
import Model.Util.OrganismType;
import Model.Util.Position;

public class Sheep extends Organism implements Movable, Eater, Reproducible {
    private static final int SHEEP_MAX_AGE = 30;
    private int energy;

    public Sheep(Position pos) {
        super(pos, SHEEP_MAX_AGE, OrganismType.SHEEP);
        this.energy = 10;
    }

    @Override
    public boolean move() {
        return false;
    }

    @Override
    public void step(Ecosystem eco) {

    }

    @Override
    public void eat(Ecosystem eco) {

    }

    @Override
    public void reproduce(Ecosystem eco) {

    }
}
