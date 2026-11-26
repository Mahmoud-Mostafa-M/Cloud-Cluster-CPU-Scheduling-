import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    private static Process[] createSampleProcesses() {
        return new Process[]{
                new Process(1, 0, 7, 2),
                new Process(2, 1, 5, 3),
                new Process(3, 3, 2, 1)
        };
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== CPU Scheduler Simulator ===");
            System.out.println("1. Preemptive SJF");
            System.out.println("2. Adaptive Container Scheduler (ACS - Tier 1 Test)");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.\n");
                scanner.next();
                continue;
            }

            if (choice == 3) {
                System.out.println("Exiting...");
                break;
            }

            if (choice != 1 && choice != 2) {
                System.out.println("Invalid choice. Please select 1, 2, or 3.\n");
                continue;
            }

            System.out.print("Enter Context Switch time (CS): ");
            int cs = scanner.nextInt();

            Process[] processes = createSampleProcesses();
            ArrayList<Process> result;

            if (choice == 1) {
                SJF sjf = new SJF();
                result = sjf.startSimulation(processes, cs);
            } else {
                System.out.print("Enter Time Quantum (Q) for Tier 1: ");
                int quantum = scanner.nextInt();
                ACS acs = new ACS();
                result = acs.startSimulation(processes, quantum, cs);
            }

            printMetrics(result);
            System.out.println();
        }

        scanner.close();
    }

    private static void printMetrics(ArrayList<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.id));

        double totalTurnaround = 0;
        double totalWaiting = 0;
        double totalResponse = 0;

        System.out.println("\nProcess | Arrival | Finish | Turnaround | Waiting | Response");
        System.out.println("------------------------------------------------------------");

        for (Process p : processes) {
            int turnaround = p.finishTime - p.arrivalTime;
            int waiting = turnaround - p.burstTime;
            int response = p.startTime - p.arrivalTime;

            totalTurnaround += turnaround;
            totalWaiting += waiting;
            totalResponse += response;

            System.out.printf("%-7s | %-7d| %-6d | %-10d | %-7d | %-8d%n",
                    p, p.arrivalTime, p.finishTime, turnaround, waiting, response);
        }

        int n = processes.size();
        System.out.println("------------------------------------------------------------");
        System.out.printf("Average Turnaround Time: %.2f%n", totalTurnaround / n);
        System.out.printf("Average Waiting Time:    %.2f%n", totalWaiting / n);
        System.out.printf("Average Response Time:   %.2f%n", totalResponse / n);
    }
}