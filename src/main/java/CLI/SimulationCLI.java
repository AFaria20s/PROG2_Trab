package CLI;

import Model.Ecosystem.Ecosystem;
import Model.Util.SimulationConfig;

import java.util.Timer;
import java.util.TimerTask;

public class SimulationCLI {
    private Ecosystem ecosystem;
    private Timer timer;

    public SimulationCLI(int gridX, int gridY) {
        ecosystem = new Ecosystem(gridX, gridY);
    }

    public void startSimulation() {
        ecosystem.initGrid();
        scheduleSimulation();
    }

    /**
     * Needs to create a new timer instance to apply the changes, if any are made.
     */
    private void scheduleSimulation() {
        // If exists, cancel
        if (this.timer != null) {
            this.timer.cancel();
        }

        // Get new delay
        long stepsPerSecond = SimulationConfig.getInstance().getSTEPS_PER_SECOND();
        long period = 1000L / stepsPerSecond;

        // Create new timer
        this.timer = new Timer();

        this.timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        ecosystem.simulateStep();

                        if (ecosystem.getStepCount() == 4) {
                            SimulationConfig.getInstance().setSTEPS_PER_SECOND(10);
                            scheduleSimulation();
                        }
                    }
                },
                period, // Start delay
                period  // Repetition delay
        );
    }
}
