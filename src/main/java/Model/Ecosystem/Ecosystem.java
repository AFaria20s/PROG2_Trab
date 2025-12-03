package Model.Ecosystem;

import Model.Organisms.Organism;
import Model.Organisms.Plant;
import Model.Organisms.Sheep;
import Model.Organisms.Wolf;
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
        System.out.println("Step: " + stepCount);
        printGrid();
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
                Organism newOrg = getOrganism(x, y, roll);

                // Se criamos um organismo, adicionamos à grelha e à lista
                if (newOrg != null) {
                    this.grid[y][x] = newOrg;
                    this.organisms.add(newOrg);
                } else {
                    this.grid[y][x] = null; // Garante que está vazio
                }
            }
        }
    }

    /**
     * Creates a new organism based on probabilities configured in the
     * SimulationConfig.java class
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param roll Random Generated Probability
     * @return Organism
     */
    private Organism getOrganism(int x, int y, double roll) {
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
}