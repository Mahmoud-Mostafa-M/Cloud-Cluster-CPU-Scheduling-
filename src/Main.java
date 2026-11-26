import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    // Test Case 1 from the assignment (Q = 3, CS = 0)
    private static Process[] createTestCase1() {
        return new Process[]{
                new Process(1, 0, 4, 2),
                new Process(2, 1, 7, 3),
                new Process(3, 3, 2, 1)
        };
    }

    // Test Case 2 from the assignment (Q = 3, CS = 1)
    private static Process[] createTestCase2() {
        return new Process[]{
                new Process(1, 0, 5, 4),
                new Process(2, 1, 6, 2),
                new Process(3, 4, 4, 3)
        };
    }

    private static Process[] readProcesses(Scanner scanner) {
        System.out.print("Number of processes: ");
        int n = scanner.nextInt();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            System.out.print("P" + (i + 1) + " - enter Arrival Time, Burst Time, Priority: ");
            int arrival = scanner.nextInt();
            int burst = scanner.nextInt();
            int priority = scanner.nextInt();
            processes[i] = new Process(i + 1, arrival, burst, priority);
        }
        return processes;
    }

    // Returns null if the input choice is invalid.
    private static Process[] chooseProcesses(Scanner scanner) {
        System.out.println("Choose the processes:");
        System.out.println("1. Test Case 1 (assignment uses Q = 3, CS = 0)");
        System.out.println("2. Test Case 2 (assignment uses Q = 3, CS = 1)");
        System.out.println("3. Enter my own processes");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            return createTestCase1();
        } else if (choice == 2) {
            return createTestCase2();
        } else if (choice == 3) {
            return readProcesses(scanner);
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== CPU Scheduler Simulator ===");
            System.out.println("1. Preemptive SJF");
            System.out.println("2. Adaptive Container Scheduler (ACS)");
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

            Process[] processes = chooseProcesses(scanner);
            if (processes == null) {
                System.out.println("Invalid choice. Please select 1, 2, or 3.\n");
                continue;
            }

            System.out.print("Enter Context Switch time (CS): ");
            int cs = scanner.nextInt();

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