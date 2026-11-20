import java.util.ArrayList;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        Process[] processes = {
                new Process(1, 0, 7, 2),
                new Process(2, 1, 5, 3),
                new Process(3, 3, 2, 1)
        };

        SJF sjf = new SJF();
        ArrayList<Process> result = sjf.startSimulation(processes, 1);
        printMetrics(result);
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