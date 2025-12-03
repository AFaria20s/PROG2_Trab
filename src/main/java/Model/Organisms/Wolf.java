package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Eater;
import Model.OrganismActions.Movable;
import Model.OrganismActions.Reproducible;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

public class Wolf extends Organism implements Movable, Eater, Reproducible {
    private int energy;

    public Wolf(Position pos) {
        super(pos, OrganismType.WOLF);
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
    protected int getMaxAgeLimit() {
        return SimulationConfig.getInstance().getWOLF_MAX_AGE();
    }

    @Override
    public void eat(Ecosystem eco) {

    }

    @Override
    public void reproduce(Ecosystem eco) {

    }
}