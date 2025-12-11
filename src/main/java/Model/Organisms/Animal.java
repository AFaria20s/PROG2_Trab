package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Animal extends Organism {
    protected int energy;
    protected Random random = new Random();

    public Animal(Position pos, OrganismType type, int startEnergy) {
        super(pos, type);
        this.energy = startEnergy;
    }

    // --- CONFIGURAÇÕES ESPECÍFICAS (Abstratas) ---
    protected abstract int getEnergyCostPerStep();
    protected abstract boolean canEat(Organism other);
    protected abstract int getEnergyGainFromFood();
    protected abstract double getReproductionProbability();
    protected abstract int getReproductionCost();
    protected abstract int getMinEnergyToReproduce();
    protected abstract Animal createOffspring(Position pos);

    @Override
    public void step(Ecosystem eco) {
        updateAge(eco);
        if (!isAlive()) return;

        // Gasto metabólico
        this.energy -= getEnergyCostPerStep();
        if (this.energy <= 0) {
            eco.removeOrganism(this);
            return;
        }

        move(eco);
        if (isAlive()) eat(eco);
        if (isAlive()) reproduce(eco);
    }

    protected void move(Ecosystem eco) {
        List<Position> adjacent = eco.getAdjacentPositions(getPosition());
        List<Position> validMoves = new ArrayList<>();

        for (Position p : adjacent) {
            Organism target = eco.getOrganismAt(p);
            if (target instanceof Empty) {
                validMoves.add(p);
            }
        }

        if (!validMoves.isEmpty()) {
            Position newPos = validMoves.get(random.nextInt(validMoves.size()));
            eco.moveOrganism(this, newPos);
        }
    }

    protected void eat(Ecosystem eco) {
        List<Position> adjacent = eco.getAdjacentPositions(getPosition());
        for (Position p : adjacent) {
            Organism target = eco.getOrganismAt(p);

            if (target != null && canEat(target)) {
                // Se for probabilístico (ex: lobo falha ataque), adicione check aqui
                // Para simplificar, assumimos sucesso se for a presa correta
                eco.removeOrganism(target);
                this.energy += getEnergyGainFromFood();
                return; // Só come um por turno
            }
        }
    }

    protected void reproduce(Ecosystem eco) {
        if (random.nextDouble() > getReproductionProbability()) return;
        if (this.energy < getMinEnergyToReproduce()) return;

        // 1. Encontrar Parceiro
        Animal partner = null;
        for (Position p : eco.getAdjacentPositions(getPosition())) {
            Organism target = eco.getOrganismAt(p);
            // Verifica se é da mesma classe (Wolf com Wolf, Sheep com Sheep) e tem energia
            if (target != null && target.getClass().equals(this.getClass())) {
                Animal possiblePartner = (Animal) target;
                if (possiblePartner.energy >= getMinEnergyToReproduce()) {
                    partner = possiblePartner;
                    break;
                }
            }
        }

        if (partner == null) return;

        // Encontrar Espaço
        Position spawnPos = eco.findAdjacentEmptyCell(getPosition());

        // Criar Filho
        if (spawnPos != null) {
            this.energy -= getReproductionCost();
            partner.energy -= getReproductionCost();
            eco.addOrganism(createOffspring(spawnPos));
        }
    }
}