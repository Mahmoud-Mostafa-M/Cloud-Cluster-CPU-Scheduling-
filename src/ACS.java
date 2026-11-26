import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class ACS {

    // Tier 1: Preemptive Round Robin queue
    Queue<Process> tier1Queue = new LinkedList<>();

    // Tier 2: Non-preemptive Priority queue (aged via Tier 3)
    ArrayList<Process> tier2Queue = new ArrayList<>();

    // Completed processes
    ArrayList<Process> completedProcesses = new ArrayList<>();

    // Timeline / Demotion Matrix log
    ArrayList<String> demotionMatrix = new ArrayList<>();

    int currentTime = 0;
    Process currentProcess = null;
    Process lastExecuted = null;
    boolean runningTier2 = false;

    public ArrayList<Process> startSimulation(Process[] processes, int quantum, int cs) {
        if (quantum <= 0 || cs < 0) {
            throw new IllegalArgumentException(
                    "Quantum must be positive and Context Switch cannot be negative."
            );
        }

        tier1Queue.clear();
        tier2Queue.clear();
        completedProcesses.clear();
        demotionMatrix.clear();

        currentTime = 0;
        currentProcess = null;
        lastExecuted = null;
        runningTier2 = false;

        boolean[] arrived = new boolean[processes.length];
        int quantumUsed = 0;
        int csRemaining = 0;
        Process pendingProcess = null;
        boolean pendingIsTier2 = false;

        while (completedProcesses.size() < processes.length) {
            // 1. Add newly arrived processes to Tier 1
            addArrivedProcesses(processes, arrived);

            // 2. If CPU is free and not mid-context-switch, select next process
            if (csRemaining == 0 && currentProcess == null) {
                Process nextCandidate = null;
                boolean nextIsTier2 = false;

                if (!tier1Queue.isEmpty()) {
                    nextCandidate = tier1Queue.poll();
                    nextIsTier2 = false;
                } else if (!tier2Queue.isEmpty()) {
                    // Tier 3 Aging: recalculate priority of waiting Tier 2 processes
                    for (Process p : tier2Queue) {
                        int waited = Math.max(0, currentTime - p.tier2EntryTime);
                        p.currPriority = Math.max(1, p.iniPriority - (waited / 4));
                    }

                    // Lower currPriority = higher priority; tie-break by tier2EntryTime, then id
                    tier2Queue.sort(
                            Comparator.comparingInt((Process p) -> p.currPriority)
                                    .thenComparingInt(p -> p.tier2EntryTime)
                                    .thenComparingInt(p -> p.id)
                    );

                    nextCandidate = tier2Queue.remove(0);
                    nextIsTier2 = true;
                }

                if (nextCandidate != null) {
                    if (cs > 0 && lastExecuted != null && lastExecuted != nextCandidate) {
                        csRemaining = cs;
                        pendingProcess = nextCandidate;
                        pendingIsTier2 = nextIsTier2;
                    } else {
                        currentProcess = nextCandidate;
                        runningTier2 = nextIsTier2;
                        quantumUsed = 0;
                    }
                }
            }

            // 3. Advance 1 tick of simulation time
            System.out.println("Time: " + currentTime);

            if (csRemaining > 0) {
                System.out.println("  Context Switching");
                recordMatrix("CONTEXT SWITCH");

                csRemaining--;
                if (csRemaining == 0) {
                    currentProcess = pendingProcess;
                    runningTier2 = pendingIsTier2;
                    pendingProcess = null;
                    quantumUsed = 0;
                }
            } else if (currentProcess != null) {
                if (currentProcess.startTime == -1) {
                    currentProcess.startTime = currentTime;
                }

                System.out.println("  Running: " + currentProcess
                        + " (Remaining: " + currentProcess.remainingTime + ")");

                recordMatrix(currentProcess + "(" + currentProcess.remainingTime + ")");

                currentProcess.remainingTime--;
                lastExecuted = currentProcess;
                if (!runningTier2) {
                    quantumUsed++;
                }

                if (currentProcess.remainingTime == 0) {
                    currentProcess.finishTime = currentTime + 1;
                    completedProcesses.add(currentProcess);
                    System.out.println("  Completed: " + currentProcess);
                    currentProcess = null;
                    runningTier2 = false;
                } else if (!runningTier2 && quantumUsed == quantum) {
                    currentProcess.tier2EntryTime = currentTime + 1;
                    tier2Queue.add(currentProcess);
                    System.out.println("  Demoted to Tier 2: " + currentProcess
                            + " (Remaining: " + currentProcess.remainingTime + ")");
                    currentProcess = null;
                    runningTier2 = false;
                }
            } else {
                System.out.println("  IDLE");
                recordMatrix("IDLE");
                lastExecuted = null;
            }

            // 4. Display queue states
            System.out.println("  Tier 1 Queue: "
                    + (tier1Queue.isEmpty() ? "[Empty]" : queueToString(tier1Queue)));
            System.out.println("  Tier 2 Queue: "
                    + (tier2Queue.isEmpty() ? "[Empty]" : queueToString(tier2Queue)));
            System.out.println("--------------------------------");

            currentTime++;
        }

        displayDemotionMatrix();
        return completedProcesses;
    }

    // Add processes that have arrived up to currentTime
    private void addArrivedProcesses(Process[] processes, boolean[] arrived) {
        for (int i = 0; i < processes.length; i++) {
            if (!arrived[i] && processes[i].arrivalTime <= currentTime) {
                tier1Queue.add(processes[i]);
                arrived[i] = true;
            }
        }
    }

    // Record each time step in Demotion Matrix
    private void recordMatrix(String runningProcess) {
        String tier1State = queueToString(tier1Queue);
        String tier2State = queueToString(tier2Queue);

        String row = String.format(
                "%-6d | %-18s | %-25s | %-25s",
                currentTime,
                runningProcess,
                tier1State,
                tier2State
        );

        demotionMatrix.add(row);
    }

    // Convert queue/list to readable String
    private String queueToString(Iterable<Process> queue) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Process p : queue) {
            if (!first) {
                result.append(", ");
            }

            result.append(p)
                    .append("(")
                    .append(p.remainingTime)
                    .append(")");

            first = false;
        }

        if (first) {
            return "Empty";
        }

        return result.toString();
    }

    // Display Demotion Matrix
    private void displayDemotionMatrix() {
        System.out.println("\n========== Demotion Matrix ==========");
        System.out.printf(
                "%-6s | %-18s | %-25s | %-25s%n",
                "Time",
                "Running",
                "Tier 1 Queue",
                "Tier 2 Queue"
        );
        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (String row : demotionMatrix) {
            System.out.println(row);
        }

        System.out.println(
                "=========================================================================="
        );
    }
}