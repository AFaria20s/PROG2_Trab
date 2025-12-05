package Model.Ecosystem;

import Model.Organisms.*;
import Model.Util.Direction;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ecosystem {
    private final int width;
    private final int height;
    private int stepCount;

    private Random random;

    private Organism[][] grid;
    private List<Organism> organisms;

    public Ecosystem(int width, int height) {
        this.width = width;
        this.height = height;
        this.stepCount = 0;

        this.grid = new Organism[height][width];
        this.organisms = new ArrayList<>();

        this.random = new Random();
    }

    /**
     * Simulates a step/year in the ecossystem
     */
    public void simulateStep() {
        stepCount++;

        // Iterar sobre uma cópia da lista de organismos ativos
        // Fazemos uma cópia para evitar ConcurrentModificationException,
        // caso um organismo morra/nasça e altere a lista durante a iteração.
        // Descobri a existencia deste erro agora lol
        List<Organism> organismsCopy = new ArrayList<>(this.organisms);

        for (Organism org : organismsCopy) {
            // Se já morreu no passo anterior, ignorar
            if (org.isAlive()) {
                org.step(this);
            }
        }

        // Limpar a lista de organismos mortos no final do passo
        // O Empty é um organismo 'vivo' para o step, mas não é processado
        this.organisms.removeIf(o -> !o.isAlive() && o.getType() != OrganismType.EMPTY);

        // 3. Imprimir a grelha
        printGrid();
    }

    /**
     * Remove um organismo (que morreu de fome/velhice) da grelha e marca-o para remoção da lista.
     */
    public void removeOrganism(Organism org) {
        if (org == null || !org.isAlive()) return;

        Position pos = org.getPosition();

        // 1. Marca o objeto como morto (para ser removido da lista 'organisms' no simulateStep())
        org.die();

        // 2. Remove da grelha, substituindo-o por Empty
        // Reutiliza a lógica de substituição do removeOrganismAt, mas chama-a diretamente.
        this.grid[pos.getY()][pos.getX()] = new Empty(pos, OrganismType.EMPTY);
    }

    /**
     * Remove o organismo na posição, marcando-o como morto e substituindo-o por Empty.
     */
    public void removeOrganismAt(Position pos) {
        // Verificação de validade
        if (!isPositionValid(pos.getX(), pos.getY())) return;

        // O índice da grelha é [Y][X]
        Organism toRemove = grid[pos.getY()][pos.getX()];

        // Marcar como morto e substituir por Empty
        if (toRemove != null && toRemove.isAlive() && toRemove.getType() != OrganismType.EMPTY) {

            // Marca o objeto como morto para ser removido da lista 'organisms'
            toRemove.die();

            // Coloca um novo Empty na grelha, garantindo que a célula está vazia
            grid[pos.getY()][pos.getX()] = new Empty(pos, OrganismType.EMPTY);
        }
    }

    /**
     * Initializes the grid creating randomly the organisms
     */
    public void initGrid() {
        // Limpar lista e grelha caso se reinicie
        this.organisms.clear();

        // Percorrer todas as posições da grelha
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                double roll = random.nextDouble(); // Gera número entre 0.0 e 1.0
                Organism newOrg = genOrganism(x, y, roll);

                // Se criamos um organismo, adicionamos à grelha e à lista
                if (newOrg != null) {
                    this.grid[y][x] = newOrg;
                    this.organisms.add(newOrg);
                } else {
                    this.grid[y][x] = new Empty(new Position(x,y),OrganismType.EMPTY); // Garante que está vazio
                }
            }
        }
    }

    public Organism getOrganismAt(int x, int y) {
        if (!isPositionValid(x, y)) {
            return new Empty(new Position(x, y), OrganismType.EMPTY); // Retorna Empty para evitar NullPointer
        }
        return grid[y][x];
    }

    /**
     * Creates a new organism based on probabilities configured in the
     * SimulationConfig.java class
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param roll Random Generated Probability
     * @return Organism
     */
    private Organism genOrganism(int x, int y, double roll) {
        Position pos = new Position(x, y);
        Organism newOrg = null;

        // Lógica de decisão baseada em probabilidades acumuladas
        if (roll < SimulationConfig.getInstance().getPROB_WOLF()) {
            newOrg = new Wolf(pos);
        }
        else if (roll < (SimulationConfig.getInstance().getPROB_WOLF() +
                            SimulationConfig.getInstance().getPROB_SHEEP())) {
            newOrg = new Sheep(pos);
        }
        else if (roll < (SimulationConfig.getInstance().getPROB_WOLF() +
                            SimulationConfig.getInstance().getPROB_SHEEP() +
                            SimulationConfig.getInstance().getPROB_PLANT())) {
            newOrg = new Plant(pos);
        }
        return newOrg;
    }

    public List<Position> getAdjacentPositions(Position pos) {
        List<Position> adjacent = new ArrayList<>();

        // Percorre todas as direções possíveis (N, S, E, O)
        for (Direction dir : Direction.values()) {
            int newX = pos.getX() + dir.getDx();
            int newY = pos.getY() + dir.getDy();

            // Verifica se a nova posição está dentro dos limites da grelha
            if (isPositionValid(newX, newY)) {
                adjacent.add(new Position(newX, newY));
            }
        }
        return adjacent;
    }

    public Position findAdjacentEmptyCell(Position center) {
        List<Position> adjacents = getAdjacentPositions(center);

        // Filtra as posições adjacentes para encontrar uma célula vazia
        for (Position p : adjacents) {
            Organism target = getOrganismAt(p.getX(), p.getY());

            // Com a classe Empty, a verificação é direta e legível!
            if (target instanceof Empty) {
                return p; // Encontrou a célula de nascimento
            }
        }

        return null; // Nenhuma célula vazia adjacente
    }

    /**
     * Move um organismo da sua posição atual para uma nova posição (x, y).
     * A célula anterior é preenchida com um objeto Empty.
     */
    public void moveOrganism(Organism org, int newX, int newY) {
        Position oldPos = org.getPosition();

        // 1. Limpa a posição antiga (coloca um novo objeto Empty)
        // Se estás a usar o construtor que só aceita Position:
        this.grid[oldPos.getY()][oldPos.getX()] = new Empty(oldPos, OrganismType.EMPTY);

        // 2. Atualiza a posição do organismo
        Position newPos = new Position(newX, newY);
        org.setPosition(newPos);

        // 3. Coloca o organismo na nova posição
        this.grid[newY][newX] = org;
    }

    /**
     * Verifica se a posição (x, y) está dentro dos limites da grelha.
     * @param x X pos
     * @param y Y pos
     * @return boolean
     */
    public boolean isPositionValid(int x, int y) {
        // Verifica limites X (largura) e limites Y (altura)
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Prints the current grid layout.
     */
    private void printGrid() {
        // Linha de cima
        System.out.print("+");
        for (int i = 0; i < this.width; i++) System.out.print("-");
        System.out.println("+");

        // Grelha
        for (int y = 0; y < this.height; y++) {
            System.out.print("|"); // Borda esquerda
            for(int x = 0; x < this.width; x++) {
                Organism o = grid[y][x];
                if (o != null) {
                    System.out.print(o.getType().getSymbol());
                } else {
                    System.out.print(OrganismType.EMPTY.getSymbol());
                }
            }
            System.out.println("|"); // Borda direita
        }

        // Linha de baixo
        System.out.print("+");
        for (int i = 0; i < this.width; i++) System.out.print("-");
        System.out.println("+");
    }

    /**
     * Get the grid's current stats
     * @return String
     */
    public String getInfo() {
        int sheep = 0;
        int wolf = 0;
        int plant = 0;
        int vazio = 0;

        for (Organism arr : organisms) {
            switch (arr) {
                case Wolf wolf1 -> wolf++;
                case Sheep sheep1 -> sheep++;
                case Plant plant1 -> plant++;
                case null, default -> vazio++;
            }
        }

        return "\nAlive: " + (sheep+wolf+plant) +
                "\nSheep: " + sheep +
                "\nWolf: " + wolf +
                "\nPlant: " + plant +
                "\nVazio: " + vazio;
    }

    public int getStepCount() {
        return this.stepCount;
    }
}