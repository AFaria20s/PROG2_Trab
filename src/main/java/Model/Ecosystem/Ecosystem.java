package Model.Ecosystem;

import Model.Organisms.*;
import Model.Util.Direction;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;
import View.Util.ToastNotification;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Representa o motor de simulação do ecossistema.
 * Esta classe gere uma grelha bidimensional de organismos, controlando o seu ciclo de vida,
 * movimentação, interações espaciais e a evolução temporal através de passos de simulação.
 * * @author Afonso Faria & Flávio Fernandes / Grupo 02
 * @version 1.0
 */
public class Ecosystem {
    /** Largura da grelha (número de colunas). */
    private final int width;

    /** Altura da grelha (número de linhas). */
    private final int height;

    /** Contador de passos decorridos desde o início da simulação. */
    private int stepCount;

    /** Gerador de números aleatórios para eventos probabilísticos. */
    private final Random random;

    /** Flag indicativa se o ecossistema ainda possui vida ativa. */
    private boolean isLifeActive;

    /** Matriz que representa a ocupação física do espaço (Verdade Espacial). */
    private final Organism[][] grid;

    /** Lista de organismos ativos para otimização de iteração (Verdade Logística). */
    private final List<Organism> organisms;

    /**
     * Constrói um novo ecossistema com as dimensões especificadas.
     *
     * @param width  Largura da grelha.
     * @param height Altura da grelha.
     */
    public Ecosystem(int width, int height) {
        this.width = width;
        this.height = height;
        this.stepCount = 0;
        this.grid = new Organism[height][width];
        this.organisms = new ArrayList<>();
        this.random = new Random();
        this.isLifeActive = true;
    }

    /** @return A largura da grelha. */
    public int getWidth() { return width; }

    /** @return A altura da grelha. */
    public int getHeight() { return height; }

    /** @return O número de passos já executados. */
    public int getStepCount() { return stepCount; }

    /** @return true se ainda houver organismos vivos (não-vazios) no sistema. */
    public boolean isLifeActive() { return this.isLifeActive; }

    /**
     * Executa um passo completo de simulação.
     * Incrementa o contador, baralha a ordem de execução dos organismos para garantir
     * equidade, executa a ação individual de cada um, tenta spawnar caçadores e
     * atualiza o estado de viabilidade do ecossistema.
     */
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

        // Verifica o estado de vida
        checkLifeStatus();
    }

    /**
     * Verifica se ainda restam organismos que não sejam do tipo 'Empty' na lista.
     * Atualiza a flag isLifeActive.
     */
    private void checkLifeStatus() {
        boolean hasLife = organisms.stream()
                .anyMatch(o -> !(o instanceof Empty));

        this.isLifeActive = hasLife;
    }

    // --- CONTROLO ESTATÍSTICO ---

    /**
     * Conta quantos organismos de um determinado tipo existem atualmente no ecossistema.
     *
     * @param type O tipo de organismo a contar.
     * @return O total de indivíduos encontrados.
     */
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

    /**
     * Adiciona um organismo ao ecossistema e regista-o na grelha física.
     *
     * @param org O organismo a ser adicionado.
     */
    public void addOrganism(Organism org) {
        Position pos = org.getPosition();
        if (isPositionValid(pos)) {
            this.grid[pos.getY()][pos.getX()] = org;
            this.organisms.add(org);
        }
    }

    /**
     * Remove um organismo do sistema, marca-o como morto e substitui a sua
     * posição na grelha por uma célula vazia (Empty).
     *
     * @param org O organismo a remover.
     */
    public void removeOrganism(Organism org) {
        if (org == null || !org.isAlive()) return;

        org.die();
        this.organisms.remove(org);

        Position pos = org.getPosition();
        if (isPositionValid(pos) && this.grid[pos.getY()][pos.getX()] == org) {
            this.grid[pos.getY()][pos.getX()] = new Empty(pos, OrganismType.EMPTY);
        }
    }

    /**
     * Remove qualquer organismo vivo que se encontre numa posição específica.
     *
     * @param pos A coordenada da célula a limpar.
     */
    public void removeOrganismAt(Position pos) {
        Organism target = getOrganismAt(pos);
        if (target != null && !(target instanceof Empty)) {
            removeOrganism(target);
        }
    }

    /**
     * Atualiza a posição de um organismo na grelha física.
     * Move a referência do organismo para as novas coordenadas e limpa a posição anterior.
     *
     * @param org    O organismo a mover.
     * @param newPos A nova posição de destino.
     */
    public void moveOrganism(Organism org, Position newPos) {
        Position oldPos = org.getPosition();

        //BUG-FIX: Remover organismo que está na posição destino ANTES de sobrescrever
        Organism targetOrg = this.grid[newPos.getY()][newPos.getX()];
        if (targetOrg != null && !(targetOrg instanceof Empty)) {
            removeOrganism(targetOrg);  // Remove da lista E marca como morto
        }

        // Limpa posição antiga
        this.grid[oldPos.getY()][oldPos.getX()] = new Empty(oldPos, OrganismType.EMPTY);
        org.setPosition(newPos);
        this.grid[newPos.getY()][newPos.getX()] = org;
    }

    /**
     * Avalia as condições para o aparecimento aleatório de caçadores.
     * Baseia-se na densidade populacional total de animais e nas probabilidades
     * configuradas em {@link SimulationConfig}.
     */
    private void attemptHunterSpawn() {
        SimulationConfig config = SimulationConfig.getInstance();
        int totalAnimals = getOrganismCountByType(OrganismType.WOLF) + getOrganismCountByType(OrganismType.SHEEP);

        if (totalAnimals < config.getHUNTER_SPAWN_THRESHOLD()) {
            return;
        }

        if (random.nextDouble() < config.getPROB_HUNTER_APPEARANCE()) {
            int spawnCount = config.getHUNTER_SPAWN_COUNT();
            for(int i = 0; i < spawnCount; i++) {
                addOrganismRandomly(OrganismType.HUNTER);
            }
        }
    }

    // --- GETTERS E HELPERS ---

    /**
     * Obtém o organismo presente numa determinada coordenada.
     *
     * @param pos A posição a consultar.
     * @return O objeto {@link Organism} na posição, ou null se a posição for inválida.
     */
    public Organism getOrganismAt(Position pos) {
        if (!isPositionValid(pos)) return null;
        return grid[pos.getY()][pos.getX()];
    }

    /**
     * Devolve uma lista de posições válidas num raio quadrado em redor de uma posição.
     *
     * @param pos    A posição central.
     * @param radius O raio de alcance.
     * @return Lista de posições adjacentes dentro do raio.
     */
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

    /**
     * Devolve as 4 posições adjacentes diretas (Norte, Sul, Este, Oeste).
     *
     * @param pos A posição central.
     * @return Lista de posições vizinhas válidas.
     */
    public List<Position> getAdjacentPositions(Position pos) {
        List<Position> adjacent = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            Position newPos = new Position(pos.getX() + dir.getDx(), pos.getY() + dir.getDy());
            if (isPositionValid(newPos)) adjacent.add(newPos);
        }
        return adjacent;
    }

    /**
     * Procura uma célula vizinha que esteja vazia (tipo Empty).
     *
     * @param center A posição a partir da qual procurar.
     * @return Uma posição vizinha livre aleatória, ou null se todas estiverem ocupadas.
     */
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

    /**
     * Verifica se uma coordenada está dentro dos limites da grelha.
     *
     * @param p A posição a validar.
     * @return true se for válida, false caso contrário.
     */
    private boolean isPositionValid(Position p) {
        return p.getX() >= 0 && p.getX() < width && p.getY() >= 0 && p.getY() < height;
    }

    // --- INIT & RESTART ---

    /**
     * Inicializa a grelha preenchendo-a com organismos aleatórios com base
     * nas probabilidades definidas na configuração inicial.
     */
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
                else if(roll < config.getPROB_WOLF_SPAWN() + config.getPROB_SHEEP_SPAWN() + config.getPROB_PLANT_SPAWN()+ 0.15) newOrg = new Bear(pos);
                else newOrg = new Empty(pos, OrganismType.EMPTY);

                this.grid[y][x] = newOrg;
                if (!(newOrg instanceof Empty)) {
                    this.organisms.add(newOrg);
                }
            }
        }
    }

    /**
     * Reinicia o ecossistema, limpando o contador de passos e gerando uma nova grelha.
     */
    public void restart() {
        this.stepCount = 0;
        this.isLifeActive = true;
        initGrid();
    }

    // --- ADIÇÃO MANUAL ---

    /**
     * Tenta adicionar um organismo de um determinado tipo numa célula vazia
     * escolhida aleatoriamente em toda a grelha.
     *
     * @param type O tipo de organismo a criar.
     * @return O organismo criado e adicionado, ou null se não houver espaço ou a vida estiver inativa.
     */
    public Organism addOrganismRandomly(OrganismType type) {
        if (!isLifeActive) return null;

        Position pos = findRandomEmptyCell();
        if (pos == null) return null;

        Organism newOrg = genOrganismAt(type, pos);
        if (newOrg != null && !(newOrg instanceof Empty)) {
            addOrganism(newOrg);
            return newOrg;
        }
        return null;
    }

    /**
     * Procura exaustivamente por células vazias na grelha.
     *
     * @return Uma posição aleatória livre, ou null se a grelha estiver cheia.
     */
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

    /**
     * Fábrica de organismos que gera uma instância concreta baseada no tipo e posição.
     *
     * @param type O tipo pretendido.
     * @param pos  A posição onde será criado.
     * @return Uma nova instância de {@link Organism}.
     */
    private Organism genOrganismAt(OrganismType type, Position pos) {
        if (type == null || type == OrganismType.EMPTY) return new Empty(pos, OrganismType.EMPTY);
        return switch (type) {
            case HUNTER -> new Hunter(pos);
            case WOLF -> new Wolf(pos);
            case SHEEP -> new Sheep(pos);
            case PLANT -> new Plant(pos);
            case BEAR -> new Bear(pos);
            case EMPTY -> new Empty(pos, OrganismType.EMPTY);
        };
    }
}
