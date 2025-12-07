package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.*;
import Model.Util.*;
import java.util.*;

public class Wolf extends Organism implements Movable, Eater, Reproducible {
    private int energy;

    public Wolf(Position pos) {
        super(pos, OrganismType.WOLF);
        this.energy = (int)(Math.random() * 50) + 20; // Energia inicial varia
    }

    public int getEnergy() { return energy; }

    @Override
    public String getDisplaySymbol() {
        if (energy > 40) return "W"; // Forte
        if (energy > 15) return "w"; // Normal
        return "w.";                 // Faminto
    }

    @Override
    public void step(Ecosystem eco) {
        if (!isAlive()) return;

        increaseAge();
        checkMaxAge();
        this.energy -= SimulationConfig.getInstance().getWOLF_ENERGY_COST_STEP();

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
        int wolfNeighbors = 0;

        for (Position p : adj) {
            Organism target = eco.getOrganismAt(p);
            // Evita Plantas para não as pisar sem querer
            if (target instanceof Empty || target.getType() == OrganismType.SHEEP) {
                valid.add(p);
            } else if (target.getType() == OrganismType.WOLF) {
                wolfNeighbors++;
            }
        }

        // Se só tem lobos à volta, fica parado
        if (valid.isEmpty() && wolfNeighbors > 0) return;

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
            if (eco.getOrganismAt(p).getType() == OrganismType.SHEEP) {
                if (Math.random() < config.getWOLF_EAT_PROB()) {
                    this.energy += config.getWOLF_ENERGY_GAIN_EAT();
                    eco.removeOrganismAt(p); // Come a ovelha
                    return; // Só come uma por turno
                }
            }
        }
    }

    @Override
    public void reproduce(Ecosystem eco) {
        SimulationConfig config = SimulationConfig.getInstance();

        if (Math.random() > config.getWOLF_REPRODUCTION_PROB()) return;
        if (this.energy < config.getWOLF_REPRODUCTION_COST()) return;

        Position spawnPos = eco.findAdjacentEmptyCell(getPosition());

        if (spawnPos != null) {
            this.energy -= config.getWOLF_REPRODUCTION_COST();
            eco.addOrganism(new Wolf(spawnPos));
        }
    }

    @Override
    protected int getMaxAgeLimit() {
        return SimulationConfig.getInstance().getWOLF_MAX_AGE();
    }
}