import CLI.SimulationCLI;
import Model.Util.OrganismType;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SimulationCLI cli = new SimulationCLI();
        Scanner scanner = new Scanner(System.in);

        System.out.println("««««« EcoSimulator »»»»»");
        System.out.println("1. Step by Step (Press Enter)");
        System.out.println("2. Run N Steps");
        System.out.println("3. Run Until Wolf Extinction");
        System.out.println("4. Run Indefinitely");
        System.out.print("Choose mode: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Press Enter to advance step. Type 'exit' to stop.");
                scanner.nextLine(); // Consumir newline
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equals("exit")) break;
                    cli.runStepByStep();
                }
                break;
            case 2:
                System.out.print("How many steps? ");
                int n = scanner.nextInt();
                cli.runNSteps(n);
                break;
            case 3:
                cli.runUntilExtinction(OrganismType.WOLF);
                break;
            case 4:
                cli.runIndefinitely();
                break;
            default:
                System.out.println("Invalid option.");
        }

        // Nota: Para os modos com Timer (2, 3, 4), o programa não termina automaticamente
        // porque o Timer thread mantém a JVM viva. Pode precisar de um mecanismo para sair.
    }
}