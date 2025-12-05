package Model.Organisms;

import Model.Ecosystem.Ecosystem;
import Model.OrganismActions.Eater;
import Model.OrganismActions.Movable;
import Model.OrganismActions.Reproducible;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wolf extends Organism implements Movable, Eater, Reproducible {
    private int energy;

    public Wolf(Position pos) {
        super(pos, OrganismType.WOLF);
        this.energy = (int)(Math.random()*100);
    }

    @Override
    public void move(Ecosystem eco) {
        Position currentPos = getPosition();
        List<Position> adjacentPositions = eco.getAdjacentPositions(currentPos);

        List<Position> validDestinations = new ArrayList<>();
        int wolfNeighbors = 0;

        // 1. Filtrar as posições adjacentes
        for (Position p : adjacentPositions) {
            Organism target = eco.getOrganismAt(p.getX(), p.getY());

            // Target será Empty, Plant, Sheep ou Wolf
            if (target instanceof Empty || target.getType() == OrganismType.SHEEP || target.getType() == OrganismType.PLANT) {
                // O lobo pode mover-se para Empty, Sheep ou Plant
                validDestinations.add(p);
            } else if (target.getType() == OrganismType.WOLF) {
                // Conta os vizinhos Lobo (para a regra de exclusão)
                wolfNeighbors++;
            }
        }

        // 2. Aplicar a Regra de Exclusão
        // Se não há destinos válidos E todos os vizinhos são lobos
        if (validDestinations.isEmpty() && wolfNeighbors == adjacentPositions.size() && !adjacentPositions.isEmpty()) {
            // O lobo fica parado e termina o movimento
            return;
        }

        // 3. Escolher o destino e mover-se
        if (!validDestinations.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(validDestinations.size());
            Position newPos = validDestinations.get(randomIndex);

            // O Ecosystem atualiza a grelha e a posição interna do lobo
            eco.moveOrganism(this, newPos.getX(), newPos.getY());
        }

        // Se validDestinations estiver vazia, mas a condição de exclusão não foi satisfeita (ex: vizinho desconhecido), o lobo também fica parado, o que é um comportamento seguro.
    }

    @Override
    public void step(Ecosystem eco) {
        if (!isAlive()) return;

        // 1. Envelhecimento e Custo de Energia
        increaseAge();
        checkMaxAge();

        // Ajustar o custo por passo para a respetiva espécie (usando Singleton)
        int cost = SimulationConfig.getInstance().getWOLF_ENERGY_COST();

        this.energy -= cost;

        // 2. Morte por Fome/Velhice
        if (this.energy <= 0 || !isAlive()) {
            eco.removeOrganism(this); // O Ecosystem gere a remoção da grelha
            return;
        }

        // 3. Movimento (move(), eat() e reproduce() devem ser implementados no teu código)
        move(eco);
        eat(eco);
        reproduce(eco);
    }

    @Override
    protected int getMaxAgeLimit() {
        return SimulationConfig.getInstance().getWOLF_MAX_AGE();
    }

    @Override
    public void eat(Ecosystem eco) {

    }

    @Override
    public void reproduce(Ecosystem eco) {

    }
}