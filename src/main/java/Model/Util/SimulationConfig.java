package Model.Util;

public class SimulationConfig {

    // --- Instância Unica ---
    private static SimulationConfig instance;

    // Construtor privado para impedir "new SimulationConfig()"
    private SimulationConfig() {}

    public static SimulationConfig getInstance() {
        if (instance == null) {
            instance = new SimulationConfig();
        }
        return instance;
    }

    // Temporizador para steps automaticos
    private int STEPS_PER_SECOND = 1;

    // Probabilidades de Spawn
    private double PROB_WOLF = 0.04;
    private double PROB_SHEEP = 0.09;
    private double PROB_PLANT = 0.11;

    // Regras da Ovelha
    private int SHEEP_MAX_AGE = 30;
    private int SHEEP_ENERGY_GAIN = 4;
    private int SHEEP_ENERGY_COST = 1;
    private double SHEEP_REPRODUCTION_PROB = 0.50;

    // Regras do Lobo
    private int WOLF_MAX_AGE = 40;
    private int WOLF_ENERGY_COST = 2;
    private double PROB_WOLF_MOVE = 0.3;
    private double PROB_WOLF_EAT = 0.5;

    // Regras da Planta
    private int PLANT_MAX_AGE = 90;

    // Getters
    public double getPROB_WOLF() { return PROB_WOLF; }
    public double getPROB_SHEEP() { return PROB_SHEEP; }
    public int getSHEEP_MAX_AGE() { return SHEEP_MAX_AGE; }
    public double getSHEEP_REPRODUCTION_PROB() { return SHEEP_REPRODUCTION_PROB; }
    public double getPROB_PLANT() { return PROB_PLANT; }
    public int getSHEEP_ENERGY_GAIN() { return SHEEP_ENERGY_GAIN; }
    public int getWOLF_MAX_AGE() { return WOLF_MAX_AGE; }
    public int getPLANT_MAX_AGE() {
        return PLANT_MAX_AGE;
    }
    public int getSTEPS_PER_SECOND() { return STEPS_PER_SECOND; }
    public double getPROB_WOLF_MOVE() { return PROB_WOLF_MOVE; }
    public int getWOLF_ENERGY_COST() { return WOLF_ENERGY_COST; }
    public int getSHEEP_ENERGY_COST() { return SHEEP_ENERGY_COST; }

    // Setters
    public void setSHEEP_REPRODUCTION_PROB(double prob) {
        this.SHEEP_REPRODUCTION_PROB = prob;
    }
    public void setWolfMaxAge(int age) {
        this.WOLF_MAX_AGE = age;
    }
    public void setWOLF_MAX_AGE(int WOLF_MAX_AGE) {
        this.WOLF_MAX_AGE = WOLF_MAX_AGE;
    }
    public void setPROB_WOLF(double PROB_WOLF) {
        this.PROB_WOLF = PROB_WOLF;
    }
    public void setPROB_SHEEP(double PROB_SHEEP) {
        this.PROB_SHEEP = PROB_SHEEP;
    }
    public void setPROB_PLANT(double PROB_PLANT) {
        this.PROB_PLANT = PROB_PLANT;
    }
    public void setSHEEP_MAX_AGE(int SHEEP_MAX_AGE) {
        this.SHEEP_MAX_AGE = SHEEP_MAX_AGE;
    }
    public void setSHEEP_ENERGY_GAIN(int SHEEP_ENERGY_GAIN) {
        this.SHEEP_ENERGY_GAIN = SHEEP_ENERGY_GAIN;
    }
    public void setPLANT_MAX_AGE(int PLANT_MAX_AGE) { this.PLANT_MAX_AGE = PLANT_MAX_AGE;}
    public void setSTEPS_PER_SECOND(int STEPS_PER_SECOND) {this.STEPS_PER_SECOND = STEPS_PER_SECOND;}
}