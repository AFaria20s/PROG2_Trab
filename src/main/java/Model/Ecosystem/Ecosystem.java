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

    // Flag para saber se o ecossistema está ativo (tem vida)
    private boolean isLifeActive;

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
        this.isLifeActive = true;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getStepCount() { return stepCount; }
    public boolean isLifeActive() { return this.isLifeActive; }

    public void simulateStep() {
        stepCount++;
        List<Organism> organismsCopy = new ArrayList<>(this.organisms);
        Collections.shuffle(organismsCopy);

        // Execução dos passos dos organismos
        for (Organism org : organismsCopy) {
            if (org.isAlive()) {
                org.step(this);
            }
        }

        attemptHunterSpawn();

        // Verifica o estado de vida (lógica apenas)
        checkLifeStatus();
    }

    private void checkLifeStatus() {
        boolean hasLife = organisms.stream()
                .anyMatch(o -> !(o instanceof Empty));

        this.isLifeActive = hasLife;
    }

    // --- CONTROLO ESTATÍSTICO ---

    public int getOrganismCountByType(OrganismType type) {
        int count = 0;
        for (Organism o : organisms) {
            if(o.getType().equals(type)) {
                count++;
            }
        }
        return count;
    }

    // --- CRUD DE ORGANISMOS ---

    public void addOrganism(Organism org) {
        Position pos = org.getPosition();
        if (isPositionValid(pos)) {
            this.grid[pos.getY()][pos.getX()] = org;
            this.organisms.add(org);
        }
    }

    public void removeOrganism(Organism org) {
        if (org == null || !org.isAlive()) return;

        org.die();
        this.organisms.remove(org);

        Position pos = org.getPosition();
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
        // Limpa posição antiga
        this.grid[oldPos.getY()][oldPos.getX()] = new Empty(oldPos, OrganismType.EMPTY);
        // Atualiza referência interna
        org.setPosition(newPos);
        // Ocupa nova posição
        this.grid[newPos.getY()][newPos.getX()] = org;
    }

    private void attemptHunterSpawn() {
        SimulationConfig config = SimulationConfig.getInstance();
        // O número total de animais (predadores + presas) tem que ser alto.
        int totalAnimals = getOrganismCountByType(OrganismType.WOLF) + getOrganismCountByType(OrganismType.SHEEP);

        if (totalAnimals < config.getHUNTER_SPAWN_THRESHOLD()) {
            return; // Não aparecem caçadores se não houver presas/concorrência suficientes.
        }

        // Verifica a chance a cada passo.
        if (random.nextDouble() < config.getPROB_HUNTER_APPEARANCE()) {

            int spawnCount = config.getHUNTER_SPAWN_COUNT();
            int addedCount = 0;

            for(int i = 0; i < spawnCount; i++) {
                Organism newHunter = addOrganismRandomly(OrganismType.HUNTER);
                if (newHunter != null) {
                    addedCount++;
                }
            }

            if (addedCount > 0) {
                System.out.println("««« HUNTER APPEARANCE: " + addedCount + " Caçador(es) chegaram! »»»");
            }
        }
    }

    // --- GETTERS E HELPERS ---

    public Organism getOrganismAt(Position pos) {
        if (!isPositionValid(pos)) return null;
        return grid[pos.getY()][pos.getX()];
    }

    public List<Position> getAdjacentPositionsRadius(Position pos, int radius) {
        List<Position> positions = new ArrayList<>();

        int startX = pos.getX() - radius;
        int endX = pos.getX() + radius;
        int startY = pos.getY() - radius;
        int endY = pos.getY() + radius;

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Position currentPos = new Position(x, y);
                if (isPositionValid(currentPos)) {
                    if (!currentPos.equals(pos)) {
                        positions.add(currentPos);
                    }
                }
            }
        }
        return positions;
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
            if (o == null || o instanceof Empty) {
                emptyPositions.add(p);
            }
        }
        if (emptyPositions.isEmpty()) return null;
        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }

    private boolean isPositionValid(Position p) {
        return p.getX() >= 0 && p.getX() < width && p.getY() >= 0 && p.getY() < height;
    }

    // --- INIT & RESTART ---

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

    public void restart() {
        this.stepCount = 0;
        this.isLifeActive = true;
        initGrid();
    }

    // --- ADIÇÃO MANUAL ---
    public Organism addOrganismRandomly(OrganismType type) {
        if (!isLifeActive) return null;

        Position pos = findRandomEmptyCell();
        if (pos == null) return null;

        Organism newOrg = genOrganismAt(type, pos);
        if (!(newOrg instanceof Empty)) {
            addOrganism(newOrg);
            return newOrg;
        }
        return null;
    }

    private Position findRandomEmptyCell() {
        List<Position> emptyPositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] instanceof Empty) {
                    emptyPositions.add(new Position(x, y));
                }
            }
        }
        if (emptyPositions.isEmpty()) return null;
        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }

    private Organism genOrganismAt(OrganismType type, Position pos) {
        if (type == null || type == OrganismType.EMPTY) return new Empty(pos, OrganismType.EMPTY);
        return switch (type) {
            case HUNTER -> new Hunter(pos);
            case WOLF -> new Wolf(pos);
            case SHEEP -> new Sheep(pos);
            case PLANT -> new Plant(pos);
            case EMPTY -> new Empty(pos, OrganismType.EMPTY);
        };
    }
}