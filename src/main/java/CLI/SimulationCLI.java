package CLI;

import Model.Ecosystem.Ecosystem;

public class SimulationCLI {
    private Ecosystem ecosystem;

    public SimulationCLI(int gridX, int gridY) {
        ecosystem = new Ecosystem(gridX, gridY);
    }

    public void startSimulation() {
        ecosystem.initGrid();
        ecosystem.simulateStep();
    }
}
