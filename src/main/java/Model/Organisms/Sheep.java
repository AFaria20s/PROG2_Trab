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

public class Sheep extends Organism implements Movable, Eater, Reproducible {
    private int energy;

    public Sheep(Position pos) {
        super(pos, OrganismType.SHEEP);
        this.energy = (int)(Math.random()*100);;
    }

    @Override
    protected int getMaxAgeLimit() {
        // Vai buscar o valor dinâmico da configuração
        return SimulationConfig.getInstance().getSHEEP_MAX_AGE();
    }

    // --- Lógica de Movimento ---
    @Override
    public void move(Ecosystem eco) {
        Position currentPos = getPosition();
        // A função getAdjacentPositions(Position) já foi corrigida
        List<Position> adjacentPositions = eco.getAdjacentPositions(currentPos);

        // Lista de células para onde a ovelha pode mover-se
        List<Position> validDestinations = new ArrayList<>();

        // 1. Filtrar as posições adjacentes
        for (Position p : adjacentPositions) {
            Organism target = eco.getOrganismAt(p.getX(), p.getY());

            // A ovelha move-se para Empty (Vazio) OU Plant (Comida)
            if (target instanceof Empty || target.getType() == OrganismType.PLANT) {
                validDestinations.add(p);
            }
            // A ovelha evita Lobos (mas neste modelo não os evita ativamente, apenas não se move para lá).
            // Se for outro organismo (Wolf ou Sheep), não é adicionado a validDestinations.
        }

        // 2. Escolher o destino e mover-se
        if (!validDestinations.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(validDestinations.size());
            Position newPos = validDestinations.get(randomIndex);

            // O Ecosystem atualiza a grelha e a posição interna da ovelha
            eco.moveOrganism(this, newPos.getX(), newPos.getY());
        }
        // Se validDestinations estiver vazia, a ovelha fica parada (comportamento seguro).
    }

    // --- Ciclo de Vida do Passo (Orquestrador) ---
    @Override
    public void step(Ecosystem eco) {
        if (!isAlive()) return;

        increaseAge();
        checkMaxAge();

        // O custo da ovelha
        int cost = SimulationConfig.getInstance().getSHEEP_ENERGY_COST();

        this.energy -= cost;

        // 2. Morte por Fome/Velhice
        // Verifica se o checkMaxAge() a matou (isAlive = false) ou se a energia chegou a zero
        if (this.energy <= 0 || !isAlive()) {
            eco.removeOrganism(this);
            return;
        }

        move(eco);
        eat(eco);
        reproduce(eco);
    }

    // --- Lógica de Alimentação (A ser implementada) ---
    @Override
    public void eat(Ecosystem eco) {
        // Lógica de comer plantas: ganha energia e remove a planta da célula

    }

    // --- Lógica de Reprodução (A ser implementada) ---
    @Override
    public void reproduce(Ecosystem eco) {
        // Lógica de encontrar parceiro, aplicar probabilidade e spawnar bebé
    }
}