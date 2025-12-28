package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Eater;
import Model.OrganismActions.Movable;
import Model.OrganismActions.Reproducible;
import Model.Util.OrganismType;
import Model.Util.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe abstrata que representa organismos móveis com necessidades energéticas.
 * Implementa os comportamentos de Movimentação, Alimentação e Reprodução Sexuada.
 */
public abstract class Animal extends Organism implements Movable, Eater, Reproducible {

    protected int energy;
    protected Random random = new Random();

    public Animal(Position pos, OrganismType type, int startEnergy) {
        super(pos, type);
        this.energy = startEnergy;
    }

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

        this.energy -= getEnergyCostPerStep();
        if (this.energy <= 0) {
            eco.removeOrganism(this);
            return;
        }

        move(eco);
        if (isAlive()) eat(eco);
        if (isAlive()) reproduce(eco);
    }

    @Override
    public void move(Ecosystem eco) {
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

    // --- IMPLEMENTAÇÃO DA INTERFACE EATER ---
    @Override
    public void eat(Ecosystem eco) {
        List<Position> adjacent = eco.getAdjacentPositions(getPosition());
        for (Position p : adjacent) {
            Organism target = eco.getOrganismAt(p);

            if (target != null && canEat(target)) {
                eco.removeOrganism(target);
                this.energy += getEnergyGainFromFood();
                return;
            }
        }
    }

    // --- IMPLEMENTAÇÃO DA INTERFACE REPRODUCIBLE ---
    @Override
    public void reproduce(Ecosystem eco) {
        if (random.nextDouble() > getReproductionProbability()) return;
        if (this.energy < getMinEnergyToReproduce()) return;

        // Encontrar Parceiro (Lógica específica de animais sexuados)
        Animal partner = null;
        for (Position p : eco.getAdjacentPositions(getPosition())) {
            Organism target = eco.getOrganismAt(p);

            // Primeiro verifica se é Animal, depois se é da mesma classe
            if (target instanceof Animal possiblePartner &&
                    target.getClass() == this.getClass()) {

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