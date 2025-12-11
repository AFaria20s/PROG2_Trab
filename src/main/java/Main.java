import CLI.SimulationCLI;
import Model.Util.OrganismType;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static SimulationCLI cli;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cli = new SimulationCLI();

        System.out.println("««««« EcoSimulator »»»»»");

        // Loop principal para o menu
        while (true) {
            displayMenu();
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consumir newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Limpar buffer
                continue;
            }

            switch (choice) {
                case 1:
                    runStepByStepMode();
                    break;
                case 2:
                    runNStepsLoop();
                    break;
                case 3:
                    runUntilExtinctionMode();
                    break;
                case 4:
                    cli.runIndefinitely();
                    System.out.println("Running indefinitely in background. Press Enter to stop and return to menu.");
                    scanner.nextLine();
                    cli.stopSimulation();
                    break;
                case 0:
                    System.out.println("Exiting simulator.");
                    cli.stopSimulation();
                    return; // Sai do método main
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Step by Step (Manual)");
        System.out.println("2. Run N Steps (Fast Forward Loop)");
        System.out.println("3. Run Until Extinction (Animated)");
        System.out.println("4. Run Indefinitely (Animated)");
        System.out.println("0. Exit");
        System.out.print("Choose mode: ");
    }

    // Novo método para Step by Step
    private static void runStepByStepMode() {
        System.out.println("--- Step by Step Mode ---");
        System.out.println("Press Enter to advance step. Type 'menu' to return to menu.");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("menu")) {
                break;
            }
            cli.runStepByStep();
        }
    }

    /**
     * Implementa o loop que corre N steps e pergunta se quer repetir.
     */
    private static void runNStepsLoop() {
        System.out.println("--- Run N Steps Mode ---");
        while (true) {
            System.out.print("How many steps to run (Type 0 or 'menu' to return to menu)? ");
            try {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("menu")) break;

                int n = Integer.parseInt(input);
                if (n <= 0) break; // Sai se for 0

                cli.runNSteps(n);

            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static void runUntilExtinctionMode() {
        System.out.println("--- Run Until Extinction Mode ---");
        System.out.print("Target Type (1-Wolf, 2-Sheep, 3-Plant): ");
        int opt = 0;
        try {
            opt = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Returning to menu.");
            scanner.nextLine();
            return;
        }

        OrganismType target = null;
        switch (opt) {
            case 1: target = OrganismType.WOLF; break;
            case 2: target = OrganismType.SHEEP; break;
            case 3: target = OrganismType.PLANT; break;
            default: System.out.println("Invalid Option. Returning to menu."); return;
        }

        cli.runUntilExtinction(target);

        // Espera que o Timer (animação) termine ou que o utilizador force a paragem
        System.out.println("Running until extinction... Press Enter to STOP and return to menu.");
        scanner.nextLine();
        cli.stopSimulation();
    }
}