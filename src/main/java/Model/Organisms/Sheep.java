package Model.Organisms;

import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

public class Sheep extends Animal {

    public Sheep(Position pos) {
        super(pos, OrganismType.SHEEP, 30);
    }

    @Override
    protected int getMaxAgeLimit() { return SimulationConfig.getInstance().getSHEEP_MAX_AGE(); }

    @Override
    protected int getEnergyCostPerStep() { return SimulationConfig.getInstance().getSHEEP_ENERGY_COST_STEP(); }

    @Override
    protected boolean canEat(Organism other) {
        return other.getType() == OrganismType.PLANT;
    }

    @Override
    protected int getEnergyGainFromFood() { return SimulationConfig.getInstance().getSHEEP_ENERGY_GAIN_EAT(); }

    @Override
    protected double getReproductionProbability() { return SimulationConfig.getInstance().getSHEEP_REPRODUCTION_PROB(); }

    @Override
    protected int getReproductionCost() { return SimulationConfig.getInstance().getSHEEP_REPRODUCTION_COST(); }

    @Override
    protected int getMinEnergyToReproduce() { return SimulationConfig.getInstance().getSHEEP_REPRODUCTION_COST(); }

    @Override
    protected Animal createOffspring(Position pos) {
        return new Sheep(pos);
    }
}