package Model.Ecosystem;

import Model.Organisms.Organism;

import java.util.List;

public class Ecosystem {
    private final int width;
    private final int height;
    private Organism[][] grid;
    private List<Organism> organisms;

    public Ecosystem(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Organism[height][width];
    }

    public void initGrid() {

    }

    public void simulateStep() {

    }
}
