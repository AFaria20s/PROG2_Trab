package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;
import java.util.ArrayList;
import java.util.List;

public class Hunter extends Animal {
    public Hunter(Position pos) {
        super(pos, OrganismType.HUNTER, 80);
    }

    @Override
    public void step(Ecosystem eco) {
        SimulationConfig config = SimulationConfig.getInstance();

        // Verificação de Envelhecimento
        updateAge(eco);
        if (!isAlive()) return; // Morreu de velhice

        // Partida por Pouca Caça (Limiar de População)
        int totalPrey = eco.getOrganismCountByType(OrganismType.WOLF) +
                eco.getOrganismCountByType(OrganismType.SHEEP);

        if (totalPrey < config.getHUNTER_DEPARTURE_THRESHOLD()) {
            eco.removeOrganism(this);
            // Hunter saiu
            /*
             * Logica que acontece quando o cacador
             * sai devido a existirem poucos animais
             */
            return;
        }

        if (this.energy >= config.getHUNTER_SATISFIED_ENERGY_THRESHOLD()) {
            double departureProb = 0.5;

            if (random.nextDouble() < departureProb) {
                eco.removeOrganism(this);
                // Hunter saiu
                /*
                 * Logica que acontece quando o cacador
                 * sai devido a sua energia ficar satisfeita
                 */
                return;
            }
        }

        // Assegura que a energia não excede o limite (evita que ele fique "imortal")
        if (this.energy > config.getHUNTER_MAX_ENERGY()) {
            this.energy = config.getHUNTER_MAX_ENERGY();
        }

        // Gasto de energia por passo
        this.energy -= getEnergyCostPerStep();
        if (this.energy <= 0) {
            eco.removeOrganism(this);
            return;
        }

        move(eco);
        if (isAlive()) eat(eco);
    }

    @Override
    public void move(Ecosystem eco) {
        List<Position> adjacent = eco.getAdjacentPositions(getPosition());
        List<Position> validMoves = new ArrayList<>();

        for (Position p : adjacent) {
            Organism target = eco.getOrganismAt(p);
            if (target instanceof Empty || target instanceof Plant) {
                validMoves.add(p);
            }
        }

        if (!validMoves.isEmpty()) {
            Position newPos = validMoves.get(random.nextInt(validMoves.size()));
            eco.moveOrganism(this, newPos);
        }
    }

    @Override
    public void eat(Ecosystem eco) {
        SimulationConfig config = SimulationConfig.getInstance();

        // Obter todas as posições num raio
        List<Position> targetsInRange = eco.getAdjacentPositionsRadius(getPosition(), config.getHUNTER_HUNT_RADIUS());
        List<Organism> preyList = new ArrayList<>();

        // Filtrar apenas o que é "comível" (Lobos e Ovelhas)
        for (Position p : targetsInRange) {
            Organism target = eco.getOrganismAt(p);
            if (target != null && canEat(target)) {
                preyList.add(target);
            }
        }

        // Se não houver nada para caçar, termina
        if (preyList.isEmpty()) return;

        // Escolher uma presa aleatória da lista
        Organism prey = preyList.get(random.nextInt(preyList.size()));

        // Calcular probabilidade de sucesso (Fórmula: Base + (Energia * Fator))
        double successChance = config.getHUNTER_BASE_HUNT_PROB() + (this.energy * config.getHUNTER_ENERGY_SKILL_FACTOR());

        if (successChance > 1.0) successChance = 1.0;

        // Tentar o disparo
        if (random.nextDouble() < successChance) {
            eco.removeOrganism(prey);
            this.energy += getEnergyGainFromFood();

            // Hunter caçou
            /*
                Logica que acontece quando o caçador caça algo
             */
        } else {
            this.energy -= 2;
        }
    }

    // --- Configurações herdadas ---

    @Override
    protected int getEnergyCostPerStep() {
        return SimulationConfig.getInstance().getHUNTER_ENERGY_COST_STEP();
    }

    @Override
    protected boolean canEat(Organism other) {
        return other.getType() == OrganismType.WOLF || other.getType() == OrganismType.SHEEP;
    }

    @Override
    protected int getEnergyGainFromFood() {
        return SimulationConfig.getInstance().getHUNTER_ENERGY_GAIN_EAT();
    }

    @Override
    protected int getMaxAgeLimit() {
        return SimulationConfig.getInstance().getHUNTER_MAX_AGE();
    }

    // --- Hunter não se reproduz (conforme seu código original) ---
    @Override protected double getReproductionProbability() { return 0; }
    @Override protected int getReproductionCost() { return 0; }
    @Override protected int getMinEnergyToReproduce() { return 0; }
    @Override protected Animal createOffspring(Position pos) { return null; }
}