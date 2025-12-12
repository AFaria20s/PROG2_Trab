package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Reproducible; // Import da Interface
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

public class Plant extends Organism implements Reproducible {

    public Plant(Position pos) {
        super(pos, OrganismType.PLANT);
    }

    @Override
    public void step(Ecosystem eco) {
        updateAge(eco);
        if(!isAlive()) return;

        reproduce(eco);
    }

    @Override
    protected int getMaxAgeLimit() {
        return SimulationConfig.getInstance().getPLANT_MAX_AGE();
    }

    @Override
    public void reproduce(Ecosystem eco) {
        // Plantas reproduzem-se assexuadamente (espalham sementes)
        if (Math.random() > SimulationConfig.getInstance().getPLANT_REPRODUCTION_PROB()) return;

        Position spawnPos = eco.findAdjacentEmptyCell(getPosition());
        if (spawnPos != null) {
            eco.addOrganism(new Plant(spawnPos));
        }
    }
}