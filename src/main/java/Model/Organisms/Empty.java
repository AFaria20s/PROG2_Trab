package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.Position;

public class Empty extends Organism {
    public Empty(Position pos, OrganismType type) {
        super(pos, type);
    }

    @Override
    public void step(Ecosystem eco) {

    }

    @Override
    protected int getMaxAgeLimit() {
        return 0;
    }
}
