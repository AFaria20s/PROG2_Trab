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

    public void simulateStep() {
        stepCount++;
        // Cópia para evitar erros ao nascer/morrer durante o loop
        List<Organism> organismsCopy = new ArrayList<>(this.organisms);

        for (Organism org : organismsCopy) {
            if (org.isAlive()) {
                org.step(this);
            }
        }

        printGrid();
        printStats();
    }

    // --- GESTÃO DE ORGANISMOS ---

    /**
     * Adicionar recém-nascidos durante a simulação
     */
    public void addOrganism(Organism org) {
        if (isPositionValid(org.getPosition().getX(), org.getPosition().getY())) {
            this.grid[org.getPosition().getY()][org.getPosition().getX()] = org;
            this.organisms.add(org);
        }
    }

    /**
     * Remoção.
     */
    public void removeOrganism(Organism org) {
        if (org == null || !org.isAlive()) return;

        Position pos = org.getPosition();
        org.die(); // Marca como morto

        // Limpa a grelha
        if (isPositionValid(pos.getX(), pos.getY())) {
            this.grid[pos.getY()][pos.getX()] = new Empty(pos, OrganismType.EMPTY);
        }
    }

    /**
     * Remove por posição
     */
    public void removeOrganismAt(Position pos) {
        Organism toRemove = getOrganismAt(pos);
        // Só remove se não for Empty e estiver vivo
        if (toRemove != null && !(toRemove instanceof Empty)) {
            removeOrganism(toRemove);
        }
    }

    public void moveOrganism(Organism org, int newX, int newY) {
        Position oldPos = org.getPosition();

        // Limpa a casa velha
        this.grid[oldPos.getY()][oldPos.getX()] = new Empty(oldPos, OrganismType.EMPTY);

        // Atualiza e coloca na casa nova
        org.setPosition(new Position(newX, newY));
        this.grid[newY][newX] = org;
    }

    // --- UTILITÁRIOS E INIT ---
    public void initGrid() {
        this.organisms.clear();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                double roll = random.nextDouble();
                Organism newOrg = generateOrganism(new Position(x,y), roll);

                if (newOrg != null) {
                    this.grid[y][x] = newOrg;
                    this.organisms.add(newOrg);
                } else {
                    this.grid[y][x] = new Empty(new Position(x,y), OrganismType.EMPTY);
                }
            }
        }
    }

    private Organism generateOrganism(Position pos, double roll) {
        SimulationConfig config = SimulationConfig.getInstance();
        if (roll < config.getPROB_WOLF_SPAWN()) return new Wolf(pos);
        else if (roll < config.getPROB_WOLF_SPAWN() + config.getPROB_SHEEP_SPAWN()) return new Sheep(pos);
        else if (roll < config.getPROB_WOLF_SPAWN() + config.getPROB_SHEEP_SPAWN() + config.getPROB_PLANT_SPAWN()) return new Plant(pos);
        return null;
    }

    public Organism getOrganismAt(Position pos) {
        if (!isPositionValid(pos.getX(), pos.getY())) return new Empty(pos, OrganismType.EMPTY);
        return grid[pos.getY()][pos.getX()];
    }

    public List<Position> getAdjacentPositions(Position pos) {
        List<Position> adjacent = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            int newX = pos.getX() + dir.getDx();
            int newY = pos.getY() + dir.getDy();
            if (isPositionValid(newX, newY)) adjacent.add(new Position(newX, newY));
        }
        return adjacent;
    }

    /**
     * Encontra uma célula vazia vizinha (crucial para reprodução)
     */
    public Position findAdjacentEmptyCell(Position center) {
        List<Position> emptyPositions = new ArrayList<>();

        for (Position p : getAdjacentPositions(center)) {
            if (getOrganismAt(p) instanceof Empty) {
                emptyPositions.add(p);
            }
        }

        if (!emptyPositions.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(emptyPositions.size());
            return emptyPositions.get(randomIndex);
        }

        return null;
    }

    public int getOrganismCountByType(OrganismType type) {
        int count = 0;
        for (Organism org : organisms) {
            if(org.isAlive() && org.getType().equals(type)) count++;
        }
        return count;
    }

    public boolean isPositionValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void printGrid() {
        System.out.print("+");
        for (int i = 0; i < this.width; i++) System.out.print("-");
        System.out.println("+");

        for (int y = 0; y < this.height; y++) {
            System.out.print("|");
            for(int x = 0; x < this.width; x++) {
                Organism o = grid[y][x];
                // Usa o método dinâmico para mostrar energia
                System.out.print(o != null ? o.getDisplaySymbol() : " ");
            }
            System.out.println("|");
        }
        System.out.print("+");
        for (int i = 0; i < this.width; i++) System.out.print("-");
        System.out.println("+");
    }

    private void printStats() {
        int w = getOrganismCountByType(OrganismType.WOLF);
        int s = getOrganismCountByType(OrganismType.SHEEP);
        int p = getOrganismCountByType(OrganismType.PLANT);
        System.out.println("Step: " + stepCount + " | Wolf: " + w + " | Sheep: " + s + " | Plant: " + p + " | Total: " + (w+s+p));
    }
}