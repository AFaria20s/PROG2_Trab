package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.*;
import Model.Util.*;
import java.util.*;

public class Sheep extends Organism implements Movable, Eater, Reproducible {
    private int energy;

    public Sheep(Position pos) {
        super(pos, OrganismType.SHEEP);
        this.energy = (int)(Math.random() * 40) + 10;
    }

    public int getEnergy() { return energy; }

    @Override
    public String getDisplaySymbol() {
        if (energy > 25) return "O";
        if (energy > 8) return "o";
        return "o.";
    }

    @Override
    public void step(Ecosystem eco) {
        if (!isAlive()) return;

        increaseAge();
        checkMaxAge();
        this.energy -= SimulationConfig.getInstance().getSHEEP_ENERGY_COST_STEP();

        if (this.energy <= 0 || !isAlive()) {
            eco.removeOrganism(this);
            return;
        }

        move(eco);
        eat(eco);
        reproduce(eco);
    }

    @Override
    public void move(Ecosystem eco) {
        List<Position> adj = eco.getAdjacentPositions(getPosition());
        List<Position> valid = new ArrayList<>();

        for (Position p : adj) {
            // Ovelha só se move para vazio (para não pisar plantas, come por vizinhança)
            if (eco.getOrganismAt(p) instanceof Empty) {
                valid.add(p);
            }
        }

        if (!valid.isEmpty()) {
            Position newPos = valid.get(new Random().nextInt(valid.size()));
            eco.moveOrganism(this, newPos.getX(), newPos.getY());
        }
    }

    @Override
    public void eat(Ecosystem eco) {
        List<Position> adj = eco.getAdjacentPositions(getPosition());
        SimulationConfig config = SimulationConfig.getInstance();

        for (Position p : adj) {
            if (eco.getOrganismAt(p).getType() == OrganismType.PLANT) {
                this.energy += config.getSHEEP_ENERGY_GAIN_EAT();
                eco.removeOrganismAt(p); // Remove a planta
                return;
            }
        }
    }

    @Override
    public void reproduce(Ecosystem eco) {
        SimulationConfig config = SimulationConfig.getInstance();

        if (Math.random() > config.getSHEEP_REPRODUCTION_PROB()) return;

        Position spawnPos = eco.findAdjacentEmptyCell(getPosition());

        if (spawnPos != null) {
            eco.addOrganism(new Sheep(spawnPos));
        }
    }

    @Override
    protected int getMaxAgeLimit() {
        return SimulationConfig.getInstance().getSHEEP_MAX_AGE();
    }
}