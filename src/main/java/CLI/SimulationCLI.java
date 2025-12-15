package CLI;

import Model.Ecosystem.Ecosystem;
import Model.Organisms.*;
import Model.Util.OrganismType;
import Model.Util.Position;
import Model.Util.SimulationConfig;

import java.util.*;

public class SimulationCLI {
    private Ecosystem ecosystem;

    private Timer timer;
    private final Scanner scanner;

    private boolean displayStatsPerStep;
    private boolean displayIndex;

    public SimulationCLI() {
        int gridX = SimulationConfig.getInstance().getWIDTH();
        int gridY = SimulationConfig.getInstance().getHEIGHT();
        this.ecosystem = new Ecosystem(gridX, gridY);
        this.ecosystem.initGrid();
        this.scanner = new Scanner(System.in);
        this.displayStatsPerStep = false;
    }

    public void start() {
        System.out.println("««««« EcoSimulator »»»»»");

        while (true) {
            displayMenu();
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1: runStepByStepMode(); break;
                case 2: runNStepsLoop(); break;
                case 3: runUntilExtinctionMode(); break;
                case 4: runIndefinitelyMode(); break;
                case 5: addOrganismMode(); break;
                case 6: restartEcosystem(); break;
                case 7: reconfigureEcosystem(); break;
                case 8: displayStatsMode(); break;
                case 9: resizeGridMode(); break;
                case 0:
                    System.out.println("Exiting simulator.");
                    stopSimulation();
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // --- VISUALIZAÇÃO ---
    private void printGrid() {
        int width = ecosystem.getWidth();
        int height = ecosystem.getHeight();

        // Borda Superior
        System.out.print(displayIndex ? " +" : "+");
        for (int i = 0; i < width; i++) System.out.print("-");
        System.out.println("+");

        // Grelha e Eixo Y
        for (int y = 0; y < height; y++) {
            System.out.print("|");
            for(int x = 0; x < width; x++) {
                Organism o = ecosystem.getOrganismAt(new Position(x, y));
                System.out.print(o.getDisplaySymbol());
            }
            System.out.println("|");
        }

        // Borda Inferior
        System.out.print(displayIndex ? " +" : "+");
        for (int i = 0; i < width; i++) System.out.print("-");
        System.out.println("+");
    }

    private void printStats() {
        int w = ecosystem.getOrganismCountByType(OrganismType.WOLF);
        int s = ecosystem.getOrganismCountByType(OrganismType.SHEEP);
        int p = ecosystem.getOrganismCountByType(OrganismType.PLANT);
        System.out.printf("Step: %d | Wolf: %d | Sheep: %d | Plant: %d%n",
                ecosystem.getStepCount(), w, s, p);
    }

    private void renderStep() {
        if (displayStatsPerStep) printStats();
        printGrid();

        if (!ecosystem.isLifeActive()) {
            printGrid();
        }
    }

    // --- MENUS E OPÇÕES ---

    private void displayMenu() {
        String statsStatus = displayStatsPerStep ? "ON" : "OFF";
        int w = SimulationConfig.getInstance().getWIDTH();
        int h = SimulationConfig.getInstance().getHEIGHT();

        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Step by Step");
        System.out.println("2. Run N Steps");
        System.out.println("3. Run Until Extinction");
        System.out.println("4. Run Indefinitely");
        System.out.println("--- ECO CONFIG ---");
        System.out.println("5. Add Organisms");
        System.out.println("6. Restart Ecossystem");
        System.out.println("7. Reconfigure Speed: " + SimulationConfig.getInstance().getSTEPS_PER_SECOND());
        System.out.println("8. Toggle Stats: " + statsStatus);
        System.out.println("9. Resize Grid: " + w + "x" + h);
        System.out.println("0. Exit");
        System.out.print("Choose mode: ");
    }

    // OPÇÃO 9: NOVA FUNÇÃO DE REDIMENSIONAMENTO
    private void resizeGridMode() {
        stopSimulation();
        System.out.println("--- Resize Grid (Warning: This will RESTART the ecosystem) ---");

        try {
            System.out.print("New Width (min 5): ");
            int newW = scanner.nextInt();
            System.out.print("New Height (min 5): ");
            int newH = scanner.nextInt();
            scanner.nextLine(); // limpar buffer

            if (newW < 5 || newH < 5) {
                System.out.println("Size too small. Operation cancelled.");
                return;
            }

            // 1. Atualizar Configuração Singleton
            SimulationConfig.getInstance().setWIDTH(newW);
            SimulationConfig.getInstance().setHEIGHT(newH);

            // 2. CRÍTICO: Criar nova instância do Ecossistema com novos tamanhos
            this.ecosystem = new Ecosystem(newW, newH);
            this.ecosystem.initGrid();

            System.out.println("Grid resized to " + newW + "x" + newH + " and restarted.");
            printGrid();

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Cancelled.");
            scanner.nextLine();
        }
    }

    private void addOrganismMode() {
        stopSimulation();
        if (!ecosystem.isLifeActive()) {
            System.out.println("Ecossystem in mass extinction. Restart first.");
            return;
        }
        System.out.println("--- Add Organism ---");
        System.out.print("Which organism to add (1-Wolf, 2-Sheep, 3-Plant, 0-Cancel)? ");
        int opt, count;
        try {
            opt = scanner.nextInt();
            scanner.nextLine();
            if(opt == 0) return;
            System.out.print("Quantity: ");
            count = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
            return;
        }

        OrganismType target = switch (opt) {
            case 1 -> OrganismType.WOLF;
            case 2 -> OrganismType.SHEEP;
            case 3 -> OrganismType.PLANT;
            default -> null;
        };

        if (target != null) {
            List<Organism> added = new ArrayList<>();
            for(int i = 0; i<count; i++) added.add(ecosystem.addOrganismRandomly(target));
            System.out.println(added.size() + " " + target.asString() + "(s) added.");
            printGrid();
        } else {
            System.out.println("Invalid type.");
        }
    }

    private void restartEcosystem() {
        stopSimulation();
        ecosystem.restart();
        System.out.println("Ecosystem restarted.");
        printGrid();
        printStats();
    }

    private void reconfigureEcosystem() {
        stopSimulation();
        System.out.println("Current SPS: " + SimulationConfig.getInstance().getSTEPS_PER_SECOND());
        System.out.print("New SPS: ");
        try {
            int newSteps = scanner.nextInt();
            scanner.nextLine();
            if (newSteps > 0) {
                SimulationConfig.getInstance().setSTEPS_PER_SECOND(newSteps);
                System.out.println("Updated.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private void displayStatsMode() {
        stopSimulation();
        this.displayStatsPerStep = !this.displayStatsPerStep;
        System.out.println("Stats per step: " + (displayStatsPerStep ? "ON" : "OFF"));
    }

    private void displayIndexMode() {
        stopSimulation();
        this.displayIndex = !this.displayIndex;
        System.out.println("Grid Index: " + (displayIndex ? "ON" : "OFF"));
        printGrid();
    }

    // --- MODOS DE EXECUÇÃO ---

    private void runStepByStepMode() {
        System.out.println("--- Step by Step (Enter to next, 'menu' to exit) ---");
        while (ecosystem.isLifeActive()) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("menu")) break;
            ecosystem.simulateStep();
            renderStep();
        }
    }

    private void runNStepsLoop() {
        System.out.print("Steps: ");
        try {
            int n = Integer.parseInt(scanner.nextLine());
            runNSteps(n);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void runUntilExtinctionMode() {
        System.out.print("Target (1-Wolf, 2-Sheep, 3-Plant): ");
        int opt = scanner.nextInt();
        scanner.nextLine();
        OrganismType target = switch (opt) {
            case 1 -> OrganismType.WOLF;
            case 2 -> OrganismType.SHEEP;
            case 3 -> OrganismType.PLANT;
            default -> null;
        };
        if(target != null) runUntilExtinction(target);
    }

    private void runIndefinitelyMode() {
        runIndefinitely();
        System.out.println("Running... Press Enter to STOP.");
        scanner.nextLine();
        stopSimulation();
    }

    // --- HELPERS EXECUÇÃO ---

    public void runNSteps(int n) {
        if(n <= 0) return;
        stopSimulation();
        int stepsCount = 0;
        while(stepsCount < n && ecosystem.isLifeActive()) {
            ecosystem.simulateStep();
            if (displayStatsPerStep) printStats();
            stepsCount++;
        }
        System.out.println("Finished " + stepsCount + " steps.");
        printGrid();
    }

    public void runUntilExtinction(OrganismType targetSpecies) {
        stopSimulation();
        long period = 1000L / SimulationConfig.getInstance().getSTEPS_PER_SECOND();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!ecosystem.isLifeActive()) {
                    System.out.println("Total Extinction.");
                    stopSimulation();
                    return;
                }
                if (ecosystem.getOrganismCountByType(targetSpecies) > 0) {
                    ecosystem.simulateStep();
                    renderStep();
                } else {
                    System.out.println(targetSpecies + " Extinct!");
                    stopSimulation();
                }
            }
        }, 0, period);
    }

    public void runIndefinitely() {
        stopSimulation();
        long period = 1000L / SimulationConfig.getInstance().getSTEPS_PER_SECOND();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!ecosystem.isLifeActive()) {
                    System.out.println("Mass Extinction.");
                    stopSimulation();
                    return;
                }
                ecosystem.simulateStep();
                renderStep();
            }
        }, 0, period);
    }

    public void stopSimulation() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }
}