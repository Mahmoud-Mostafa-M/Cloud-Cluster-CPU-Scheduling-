import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ACS {

    // Tier 1: Preemptive Round Robin queue
    Queue<Process> tier1Queue = new LinkedList<>();

    // Tier 2: Non-preemptive Priority queue (Tier2.java)
    Tier2 tier2 = new Tier2();

    // Tier 3: Aging (Tier3.java)
    Tier3 tier3 = new Tier3();

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
        tier2 = new Tier2();
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
                } else if (!tier2.isEmpty()) {
                    // Tier 3 Aging: recalculate priority of waiting Tier 2 processes
                    tier3.applyAging(tier2, currentTime);

                    nextCandidate = tier2.pollNextProcess();
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
                } else if (!runningTier2
                        && tier2.checkDemotion(currentProcess, quantumUsed, quantum, currentTime + 1)) {
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
                    + (tier2.isEmpty() ? "[Empty]" : queueToString(tier2.getWaitingProcesses())));
            System.out.println("--------------------------------");

            currentTime++;
        }
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
        String tier2State = queueToString(tier2.getWaitingProcesses());

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
}