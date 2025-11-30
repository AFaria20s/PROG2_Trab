package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Eater;
import Model.OrganismActions.Movable;
import Model.OrganismActions.Reproducible;
import Model.Util.OrganismType;
import Model.Util.Position;

public class Wolf extends Organism implements Movable, Eater, Reproducible {
    private static final int WOLF_MAX_AGE = 40;
    private int energy;

    public Wolf(Position pos) {
        super(pos, WOLF_MAX_AGE, OrganismType.WOLF);
        this.energy = 15;
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