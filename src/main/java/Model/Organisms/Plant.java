package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Reproducible;
import Model.Util.OrganismType;
import Model.Util.Position;

public class Plant extends Organism implements Reproducible {
    private static final int PLANT_MAX_AGE = 20;

    public Plant(Position pos) {
        super(pos, PLANT_MAX_AGE, OrganismType.PLANT);
    }

    @Override
    public void step(Ecosystem eco) {

    }

    @Override
    public void reproduce(Ecosystem eco) {

    }
}
