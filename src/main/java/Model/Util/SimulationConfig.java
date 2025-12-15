package Model.Util;

public class SimulationConfig {

    private static SimulationConfig instance;
    private SimulationConfig() {}

    public static SimulationConfig getInstance() {
        if (instance == null) {
            instance = new SimulationConfig();
        }
        return instance;
    }

    // --- GRID ---
    private int WIDTH = 60;
    private int HEIGHT = 15;

    // --- TEMPO ---
    private int STEPS_PER_SECOND = 15;

    // === PROBABILIDADES INICIAIS DE SPAWN ===
    private double PROB_WOLF_SPAWN = 0.035;
    private double PROB_SHEEP_SPAWN = 0.08;
    private double PROB_PLANT_SPAWN = 0.20;

    // === REGRAS DA OVELHA ===
    private int SHEEP_MAX_AGE = 35;
    private int SHEEP_ENERGY_GAIN_EAT = 15;
    private int SHEEP_ENERGY_COST_STEP = 1;
    private double SHEEP_REPRODUCTION_PROB = 0.50;
    private int SHEEP_REPRODUCTION_COST = 15;
    private double SHEEP_EAT_PROB = 0.6;

    // === REGRAS DO LOBO ===
    private int WOLF_MAX_AGE = 40;
    private int WOLF_ENERGY_GAIN_EAT = 40;
    private int WOLF_ENERGY_COST_STEP = 1;
    private double WOLF_REPRODUCTION_PROB = 0.15;
    private int WOLF_REPRODUCTION_COST = 40;
    private double WOLF_EAT_PROB = 0.80;

    // === REGRAS DA PLANTA ===
    private int PLANT_MAX_AGE = 30;
    private double PLANT_REPRODUCTION_PROB = 0.15;

    // --- REGRAS DO CACADOR ---
    private double PROB_HUNTER_APPEARANCE = 0.03;
    private int HUNTER_SPAWN_THRESHOLD = 15;
    private int HUNTER_DEPARTURE_THRESHOLD = 15;

    private int HUNTER_MAX_ENERGY = 150;
    private int HUNTER_SATISFIED_ENERGY_THRESHOLD = 100;

    private int HUNTER_SPAWN_COUNT = 1;
    private int HUNTER_MAX_AGE = 80;
    private int HUNTER_ENERGY_GAIN_EAT = 50;
    private int HUNTER_ENERGY_COST_STEP = 2;
    private int HUNTER_HUNT_RADIUS = 4;
    private double HUNTER_BASE_HUNT_PROB = 0.40;
    private double HUNTER_ENERGY_SKILL_FACTOR = 0.006;

    // --- GETTERS ---
    public int getHEIGHT() {return HEIGHT;}
    public int getWIDTH() {return WIDTH;}
    public int getSTEPS_PER_SECOND() { return STEPS_PER_SECOND; }
    public double getPROB_WOLF_SPAWN() { return PROB_WOLF_SPAWN; }
    public double getPROB_SHEEP_SPAWN() { return PROB_SHEEP_SPAWN; }
    public double getPROB_PLANT_SPAWN() { return PROB_PLANT_SPAWN; }
    public int getSHEEP_MAX_AGE() { return SHEEP_MAX_AGE; }
    public int getSHEEP_ENERGY_GAIN_EAT() { return SHEEP_ENERGY_GAIN_EAT; }
    public int getSHEEP_ENERGY_COST_STEP() { return SHEEP_ENERGY_COST_STEP; }
    public double getSHEEP_REPRODUCTION_PROB() { return SHEEP_REPRODUCTION_PROB; }
    public int getSHEEP_REPRODUCTION_COST() { return SHEEP_REPRODUCTION_COST; }
    public int getWOLF_MAX_AGE() { return WOLF_MAX_AGE; }
    public int getWOLF_ENERGY_GAIN_EAT() { return WOLF_ENERGY_GAIN_EAT; }
    public int getWOLF_ENERGY_COST_STEP() { return WOLF_ENERGY_COST_STEP; }
    public double getWOLF_REPRODUCTION_PROB() { return WOLF_REPRODUCTION_PROB; }
    public int getWOLF_REPRODUCTION_COST() { return WOLF_REPRODUCTION_COST; }
    public double getWOLF_EAT_PROB() { return WOLF_EAT_PROB; }
    public int getPLANT_MAX_AGE() { return PLANT_MAX_AGE; }
    public double getPLANT_REPRODUCTION_PROB() { return PLANT_REPRODUCTION_PROB; }
    public double getPROB_HUNTER_APPEARANCE() { return PROB_HUNTER_APPEARANCE; }
    public int getHUNTER_SPAWN_THRESHOLD() { return HUNTER_SPAWN_THRESHOLD; }
    public int getHUNTER_SPAWN_COUNT() { return HUNTER_SPAWN_COUNT; }
    public int getHUNTER_MAX_AGE() { return HUNTER_MAX_AGE; }
    public int getHUNTER_ENERGY_GAIN_EAT() { return HUNTER_ENERGY_GAIN_EAT; }
    public int getHUNTER_ENERGY_COST_STEP() { return HUNTER_ENERGY_COST_STEP; }
    public int getHUNTER_HUNT_RADIUS() { return HUNTER_HUNT_RADIUS; }
    public double getHUNTER_BASE_HUNT_PROB() { return HUNTER_BASE_HUNT_PROB; }
    public double getHUNTER_ENERGY_SKILL_FACTOR() { return HUNTER_ENERGY_SKILL_FACTOR; }
    public int getHUNTER_DEPARTURE_THRESHOLD() { return HUNTER_DEPARTURE_THRESHOLD; }
    public double getSHEEP_EAT_PROB() {return SHEEP_EAT_PROB;}
    public int getHUNTER_MAX_ENERGY() { return HUNTER_MAX_ENERGY; }
    public int getHUNTER_SATISFIED_ENERGY_THRESHOLD() { return HUNTER_SATISFIED_ENERGY_THRESHOLD; }

    // --- SETTERS ---
    public void setSTEPS_PER_SECOND(int steps) { this.STEPS_PER_SECOND = steps; }
    public void setWIDTH(int width) { this.WIDTH = width; }
    public void setHEIGHT(int height) { this.HEIGHT = height; }
}