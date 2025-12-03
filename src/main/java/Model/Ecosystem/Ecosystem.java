package Model.Ecosystem;

import Model.Organisms.Organism;
import Model.Organisms.Plant; // Importa as tuas classes concretas
import Model.Organisms.Sheep;
import Model.Organisms.Wolf;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig; // A classe que criamos acima

import java.util.ArrayList; // Importante!
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

    public void simulateStep() {
        stepCount++;
        System.out.println("Step: " + stepCount);
        printGrid();
    }
}