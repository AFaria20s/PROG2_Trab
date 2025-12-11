package Model.Ecosystem;

import Model.Organisms.*;
import Model.Util.Direction;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Ecosystem {
    private final int width;
    private final int height;
    private int stepCount;
    private final Random random;

    // A Grid é a "verdade" espacial, a Lista é a "verdade" para iteração
    private final Organism[][] grid;
    private final List<Organism> organisms;

    public Ecosystem(int width, int height) {
        this.width = width;
        this.height = height;
        this.stepCount = 0;
        this.grid = new Organism[height][width];
        this.organisms = new ArrayList<>();
        this.random = new Random();
    }

    public void simulateStep() {
        stepCount++;
        // Cópia para evitar ConcurrentModificationException
        List<Organism> organismsCopy = new ArrayList<>(this.organisms);

        // Baralhar para não haver vantagem de ordem na lista
        Collections.shuffle(organismsCopy);

        for (Organism org : organismsCopy) {
            // Verifica se ainda está vivo antes de agir
            if (org.isAlive()) {
                org.step(this);
            }
        }

        // Limpeza de mortos da lista principal acontece via removeOrganism durante o loop
        //printStats();
        printGrid();
    }

    public int getOrganismCountByType(OrganismType type) {
        int count = 0;
        for (Organism o : organisms) {
            if(o.getType().equals(type)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Imprime o estado atual da grelha na consola, usando os símbolos de display.
     */
    public void printGrid() {
        // Imprime a borda superior
        System.out.print("+");
        for (int i = 0; i < this.width; i++) System.out.print("-");
        System.out.println("+");

        // Itera sobre a grelha e imprime os organismos
        for (int y = 0; y < this.height; y++) {
            System.out.print("|");
            for(int x = 0; x < this.width; x++) {
                Organism o = grid[y][x];
                System.out.print(o.getDisplaySymbol());
            }
            System.out.println("|");
        }

        // Imprime a borda inferior
        System.out.print("+");
        for (int i = 0; i < this.width; i++) System.out.print("-");
        System.out.println("+");
    }

    // --- CRUD DE ORGANISMOS ---

    public void addOrganism(Organism org) {
        Position pos = org.getPosition();
        if (isPositionValid(pos)) {
            // Se já houver algo lá que não seja Empty, não sobrepomos sem lógica
            // Mas assumimos que o chamador (reproduce) já verificou espaço vazio
            this.grid[pos.getY()][pos.getX()] = org;
            this.organisms.add(org);
        }
    }

    public void removeOrganism(Organism org) {
        if (org == null || !org.isAlive()) return; // Já está morto/removido

        org.die(); // Marca flag interna como morto
        this.organisms.remove(org); // Remove da lista

        Position pos = org.getPosition();
        // Remove da grid apenas se o organismo na grid for de facto este
        // (Previne bugs onde removemos um organismo que já se moveu ou foi substituído)
        if (isPositionValid(pos) && this.grid[pos.getY()][pos.getX()] == org) {
            this.grid[pos.getY()][pos.getX()] = new Empty(pos, OrganismType.EMPTY);
        }
    }

    public void removeOrganismAt(Position pos) {
        Organism target = getOrganismAt(pos);
        if (target != null && !(target instanceof Empty)) {
            removeOrganism(target);
        }
    }

    public void moveOrganism(Organism org, Position newPos) {
        Position oldPos = org.getPosition();

        // 1. Limpa posição antiga
        this.grid[oldPos.getY()][oldPos.getX()] = new Empty(oldPos, OrganismType.EMPTY);

        // 2. Atualiza referência interna
        org.setPosition(newPos);

        // 3. Ocupa nova posição
        this.grid[newPos.getY()][newPos.getX()] = org;
    }

    // --- GETTERS E HELPERS ---

    public Organism getOrganismAt(Position pos) {
        if (!isPositionValid(pos)) return null;
        return grid[pos.getY()][pos.getX()];
    }

    public List<Position> getAdjacentPositions(Position pos) {
        List<Position> adjacent = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            Position newPos = new Position(pos.getX() + dir.getDx(), pos.getY() + dir.getDy());
            if (isPositionValid(newPos)) adjacent.add(newPos);
        }
        return adjacent;
    }

    public Position findAdjacentEmptyCell(Position center) {
        List<Position> emptyPositions = new ArrayList<>();
        for (Position p : getAdjacentPositions(center)) {
            Organism o = getOrganismAt(p);
            if (o == null || o instanceof Empty) { // Null check safe
                emptyPositions.add(p);
            }
        }
        if (emptyPositions.isEmpty()) return null;
        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }

    private boolean isPositionValid(Position p) {
        return p.getX() >= 0 && p.getX() < width && p.getY() >= 0 && p.getY() < height;
    }

    // --- INIT ---

    public void initGrid() {
        this.organisms.clear();
        SimulationConfig config = SimulationConfig.getInstance();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Position pos = new Position(x, y);
                double roll = random.nextDouble();
                Organism newOrg;

                if (roll < config.getPROB_WOLF_SPAWN()) newOrg = new Wolf(pos);
                else if (roll < config.getPROB_WOLF_SPAWN() + config.getPROB_SHEEP_SPAWN()) newOrg = new Sheep(pos);
                else if (roll < config.getPROB_WOLF_SPAWN() + config.getPROB_SHEEP_SPAWN() + config.getPROB_PLANT_SPAWN()) newOrg = new Plant(pos);
                else newOrg = new Empty(pos, OrganismType.EMPTY);

                this.grid[y][x] = newOrg;
                if (!(newOrg instanceof Empty)) {
                    this.organisms.add(newOrg);
                }
            }
        }
    }

    private void printStats() {
        long w = organisms.stream().filter(o -> o instanceof Wolf).count();
        long s = organisms.stream().filter(o -> o instanceof Sheep).count();
        long p = organisms.stream().filter(o -> o instanceof Plant).count();
        System.out.printf("Step: %d | Wolf: %d | Sheep: %d | Plant: %d%n", stepCount, w, s, p);
    }
}