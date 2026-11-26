import java.util.ArrayList;

public class ACS {

    Tier1 tier1 = new Tier1();
    Tier2 tier2 = new Tier2();
    Tier3 tier3 = new Tier3();
    ArrayList<Process> completedProcesses = new ArrayList<>();

    Process[] processes;
    boolean[] arrived;

    int currentTime = 0;
    Process lastExecuted = null;

    public ArrayList<Process> startSimulation(Process[] processes, int quantum, int cs) {

        if (quantum <= 0 || cs < 0) {
            throw new IllegalArgumentException(
                    "Quantum must be positive and Context Switch cannot be negative."
            );
        }

        tier1 = new Tier1();
        tier2 = new Tier2();
        completedProcesses.clear();
        this.processes = processes;
        arrived = new boolean[processes.length];
        currentTime = 0;
        lastExecuted = null;

        while (completedProcesses.size() < processes.length) {

            addArrivals();

            if (!tier1.isEmpty()) {
                // Tier 1: Round Robin
                Process nextProcess = tier1.pollNextProcess();
                contextSwitch(nextProcess, cs);
                runTier1(nextProcess, quantum);

            } else if (!tier2.isEmpty()) {
                // Tier 2 only runs when Tier 1 is empty; aging (Tier 3) is applied before choosing
                System.out.println("Time: " + currentTime + "  Tier 1 is empty -> Tier 2 chooses (aging):");
                tier3.applyAging(tier2, currentTime);
                Process nextProcess = tier2.startNextProcess();
                contextSwitch(nextProcess, cs);
                runTier2(nextProcess);

            } else {
                printTimeStep("IDLE");
                currentTime++;
            }
        }

        return completedProcesses;
    }

    // Run a Tier 1 process for at most one quantum.
    private void runTier1(Process currentProcess, int quantum) {
        int timeUsed = 0;

        while (timeUsed < quantum && currentProcess.remainingTime > 0) {

            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            printTimeStep("Running: " + currentProcess + " [Tier 1] (remaining: "
                    + currentProcess.remainingTime + ")");

            currentProcess.remainingTime--;
            timeUsed++;
            currentTime++;
            addArrivals();
        }

        lastExecuted = currentProcess;

        if (currentProcess.remainingTime == 0) {
            currentProcess.finishTime = currentTime;
            completedProcesses.add(currentProcess);
            System.out.println("  Completed: " + currentProcess);

        } else if (tier2.checkDemotion(currentProcess, timeUsed, quantum, currentTime)) {
            System.out.println("  Demoted to Tier 2: " + currentProcess
                    + " (remaining: " + currentProcess.remainingTime + ")");
        }
    }

    // Run a Tier 2 process until it finishes. Nothing can preempt it.
    private void runTier2(Process currentProcess) {
        boolean finished = false;

        while (!finished) {
            printTimeStep("Running: " + currentProcess + " [Tier 2] (remaining: "
                    + currentProcess.remainingTime + ")");

            finished = tier2.runOneTimeUnit(currentTime);
            currentTime++;
            addArrivals(); // new arrivals wait in Tier 1 until this process is done
        }

        lastExecuted = currentProcess;
        completedProcesses.add(currentProcess);
        System.out.println("  Completed: " + currentProcess);
    }

    // Spend cs time units switching to nextProcess (only if it is a different process).
    private void contextSwitch(Process nextProcess, int cs) {
        if (cs == 0 || lastExecuted == null || lastExecuted == nextProcess) {
            return;
        }

        for (int i = 0; i < cs; i++) {
            String message = "Context switch ";
            if (lastExecuted.remainingTime > 0) {
                message += "saves " + lastExecuted + " and ";
            }
            printTimeStep(message + "loads " + nextProcess);

            currentTime++;
            addArrivals(); // processes can arrive during a context switch
        }
    }

    private void addArrivals() {
        for (int i = 0; i < processes.length; i++) {
            if (!arrived[i] && processes[i].arrivalTime <= currentTime) {
                tier1.addProcess(processes[i]);
                arrived[i] = true;
            }
        }
    }

    private void printTimeStep(String event) {
        System.out.println("Time: " + currentTime + "  " + event);
        System.out.print("  ");
        tier1.printQueue();
        System.out.print("  ");
        tier2.printQueue();
    }
}
