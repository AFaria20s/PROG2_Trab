package CLI;

import Model.Ecosystem.Ecosystem;
import Model.Util.OrganismType;
import Model.Util.SimulationConfig;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class SimulationCLI {
    private Ecosystem ecosystem;
    private Timer timer;
    private boolean isRunning;

    public SimulationCLI() {
        int gridX = SimulationConfig.getInstance().getWIDTH();
        int gridY = SimulationConfig.getInstance().getHEIGHT();
        ecosystem = new Ecosystem(gridX, gridY);
        ecosystem.initGrid(); // Inicializa logo
    }

    // --- MÉTODOS DE EXECUÇÃO ---

    /**
     * Opção 1: Correr Passo a Passo (Manual)
     */
    public void runStepByStep() {
        stopSimulation(); // Garante que não há timer ativo
        System.out.println("--- Executing Single Step ---");
        ecosystem.simulateStep();
    }

    /**
     * Opção 2: Correr N Passos (Automático, com delay)
     */
    public void runNSteps(int n) {
        stopSimulation();
        System.out.println("--- Running for " + n + " steps ---");
        int stepsCount = 0;

        while(stepsCount<n) {
            ecosystem.simulateStep();
            stepsCount++;
        }
        System.out.println("--- Finished " + n + " steps ---");
        stopSimulation(); // Cancela o timer
    }

    /**
     * Opção 3: Correr até Extinção de uma Espécie
     */
    public void runUntilExtinction(OrganismType targetSpecies) {
        stopSimulation();
        System.out.println("--- Running until extinction of " + targetSpecies + " ---");

        long period = 1000L / SimulationConfig.getInstance().getSTEPS_PER_SECOND();
        this.timer = new Timer();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Verifica se a espécie ainda existe
                int count = ecosystem.getOrganismCountByType(targetSpecies);

                if (count > 0) {
                    ecosystem.simulateStep();
                } else {
                    System.out.println("--- EXTINCTION EVENT: " + targetSpecies + " has disappeared! ---");
                    stopSimulation();
                }
            }
        }, 0, period);
    }

    /**
     * Opção Extra: Correr indefinidamente (o seu modo original)
     */
    public void runIndefinitely() {
        stopSimulation();
        System.out.println("--- Running Indefinitely ---");

        long period = 1000L / SimulationConfig.getInstance().getSTEPS_PER_SECOND();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ecosystem.simulateStep();
            }
        }, 0, period);
    }

    /**
     * Para qualquer simulação em curso.
     */
    public void stopSimulation() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }
}