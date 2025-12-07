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

    // --- TEMPO ---
    private int STEPS_PER_SECOND = 3;

    // === PROBABILIDADES INICIAIS DE SPAWN (Ajustadas para menos Lobos) ===
    // Reduzida: Para iniciar com menos Lobos (cerca de 10-15) e dar espaço às Ovelhas.
    private double PROB_WOLF_SPAWN = 0.035;
    private double PROB_SHEEP_SPAWN = 0.09;
    private double PROB_PLANT_SPAWN = 0.20;

    // === REGRAS DA OVELHA (Ajustadas para Maior Resiliência) ===
    private int SHEEP_MAX_AGE = 25;
    private int SHEEP_ENERGY_GAIN_EAT = 10;
    // Reduzido: Voltar ao custo 2; custo 3 era demasiado penalizador.
    private int SHEEP_ENERGY_COST_STEP = 2;
    // Aumentado: Dar maior capacidade de recuperação populacional.
    private double SHEEP_REPRODUCTION_PROB = 0.07;
    private int SHEEP_REPRODUCTION_COST = 15;

    // === REGRAS DO LOBO (Mantidas - Dinâmica Predador/Presa OK) ===
    private int WOLF_MAX_AGE = 30;
    private int WOLF_ENERGY_GAIN_EAT = 15;
    private int WOLF_ENERGY_COST_STEP = 2;
    private double WOLF_REPRODUCTION_PROB = 0.03;
    private int WOLF_REPRODUCTION_COST = 30;
    private double WOLF_EAT_PROB = 0.60;

    // === REGRAS DA PLANTA (Mantidas - Este ajuste funcionou) ===
    private int PLANT_MAX_AGE = 100;
    // Mantido: A planta precisa de alta taxa de regeneração para aguentar o consumo.
    private double PLANT_REPRODUCTION_PROB = 0.50;

    // --- GETTERS ---
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

    // --- SETTERS ---
    public void setSTEPS_PER_SECOND(int steps) { this.STEPS_PER_SECOND = steps; }
}