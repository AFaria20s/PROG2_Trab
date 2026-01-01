package Model.Organisms;

import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

public class Bear extends Animal{

    public Bear(Position pos) {
        super(pos, OrganismType.BEAR, 100);
    }

    @Override
    protected int getEnergyCostPerStep() {
        return 3;
    }

    @Override
    protected boolean canEat(Organism other) {
        if(Math.random() < 0.2) {
            return other.getType() == OrganismType.WOLF || other.getType() == OrganismType.SHEEP;
        }
        return false;
    }

    @Override
    protected int getEnergyGainFromFood() {
        return 30;
    }

    @Override
    protected double getReproductionProbability() {
        return 0.7;
    }

    @Override
    protected int getReproductionCost() {
        return 20;
    }

    @Override
    protected int getMinEnergyToReproduce() {
        return 30;
    }

    @Override
    protected Animal createOffspring(Position pos) {
        return new Bear(pos);
    }

    @Override
    protected int getMaxAgeLimit() {
        return 90;
    }
}
