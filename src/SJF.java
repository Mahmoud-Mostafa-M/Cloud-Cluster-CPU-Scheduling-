import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ArrayList;

public class SJF {
    PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.remainingTime)
                    .thenComparingInt(p -> p.arrivalTime)
                    .thenComparingInt(p -> p.id)
    );
    int currentTime = 0;
    Process currentProcess = null, lastExecuted = null;
    ArrayList<Process> completedProcesses = new ArrayList<>();

    public ArrayList<Process> startSimulation(Process[] processes, int cs) {
        int csRemaining = 0;

        while (completedProcesses.size() < processes.length) {
            // add process to queue when it arrives
            for (Process p : processes) {
                if (currentTime == p.arrivalTime) readyQueue.add(p);
            }

            // return current process to queue if shorter process arrives
            if (currentProcess != null && !readyQueue.isEmpty() && readyQueue.peek().remainingTime < currentProcess.remainingTime) {
                readyQueue.add(currentProcess);
                currentProcess = null;
            }

            // trigger context switching
            if (csRemaining == 0 && currentProcess == null && !readyQueue.isEmpty()) {
                if (cs > 0 && lastExecuted != null && lastExecuted != readyQueue.peek()) csRemaining = cs;
                else currentProcess = readyQueue.poll();
            }

            System.out.println("Time: " + currentTime);
            if (csRemaining > 0) {
                System.out.println("  Context switch " + (lastExecuted.remainingTime > 0 ? "saves " + lastExecuted + " and " : "") + "loads " + readyQueue.peek());
                if (--csRemaining == 0) {
                    currentProcess = readyQueue.poll();
                    lastExecuted = currentProcess;
                }
            } else if (currentProcess != null) {
                if (currentProcess.remainingTime == currentProcess.burstTime) currentProcess.startTime = currentTime;
                System.out.println("  Running: " + currentProcess + " (remaining: " + currentProcess.remainingTime + ")");
                lastExecuted = currentProcess;

                if (--currentProcess.remainingTime == 0) {
                    currentProcess.finishTime = currentTime + 1;
                    completedProcesses.add(currentProcess);
                    currentProcess = null;
                }
            } else {
                System.out.println("  IDLE");
            }

            // print Ready Queue
            System.out.println("  Ready Queue:");
            if (readyQueue.isEmpty()) System.out.println("    [Empty]");
            else for (Process p : readyQueue) System.out.println("    " + p + " (remaining: " + p.remainingTime + ")");

            currentTime++;
        }
        return completedProcesses;
    }
}