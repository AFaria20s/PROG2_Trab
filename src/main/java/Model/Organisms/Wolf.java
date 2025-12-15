package Model.Organisms;

import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

public class Wolf extends Animal {

    public Wolf(Position pos) {
        super(pos, OrganismType.WOLF, 50);
    }

    @Override
    protected int getMaxAgeLimit() { return SimulationConfig.getInstance().getWOLF_MAX_AGE(); }

    @Override
    protected int getEnergyCostPerStep() { return SimulationConfig.getInstance().getWOLF_ENERGY_COST_STEP(); }

    @Override
    protected boolean canEat(Organism other) {
        if(Math.random() < SimulationConfig.getInstance().getWOLF_EAT_PROB()) {
            return other.getType() == OrganismType.SHEEP;
        }
        return false;
    }

    @Override
    protected int getEnergyGainFromFood() { return SimulationConfig.getInstance().getWOLF_ENERGY_GAIN_EAT(); }

    @Override
    protected double getReproductionProbability() { return SimulationConfig.getInstance().getWOLF_REPRODUCTION_PROB(); }

    @Override
    protected int getReproductionCost() { return SimulationConfig.getInstance().getWOLF_REPRODUCTION_COST(); }

    @Override
    protected int getMinEnergyToReproduce() { return SimulationConfig.getInstance().getWOLF_REPRODUCTION_COST(); }

    @Override
    protected Animal createOffspring(Position pos) {
        return new Wolf(pos);
    }
}