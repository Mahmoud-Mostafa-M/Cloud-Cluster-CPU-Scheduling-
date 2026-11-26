import java.util.ArrayList;

// Tier 2: Non-Preemptive Priority queue for processes demoted from Tier 1.
public class Tier2 {

    // Once a process is placed here it stays until it finishes, which makes Tier 2 non-preemptive.
    private Process runningProcess = null;

    // Insertion order is kept, so equal priorities are served first come, first served.
    private final ArrayList<Process> readyQueue = new ArrayList<>();

    // Time each process entered Tier 2, used by Tier 3 for aging.
    private final ArrayList<Integer> enteredTier2At = new ArrayList<>();

    public void addProcess(Process process, int currentTime) {
        if (process == null) {
            return;
        }

        if (readyQueue.contains(process)) {
            return;
        }

        readyQueue.add(process);
        enteredTier2At.add(currentTime);
    }

    // A process that used its whole quantum without finishing is a CPU-bound batch job.
    public boolean checkDemotion(Process process, int timeUsed, int quantum, int currentTime) {
        if (timeUsed == quantum && process.remainingTime > 0) {
            addProcess(process, currentTime);
            return true;
        }
        return false;
    }

    // Smaller priority number = higher priority.
    public Process peekNextProcess() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        int bestIndex = 0;

        for (int i = 1; i < readyQueue.size(); i++) {
            Process current = readyQueue.get(i);
            Process best = readyQueue.get(bestIndex);

            if (current.currPriority < best.currPriority) {
                bestIndex = i;
            }
        }

        return readyQueue.get(bestIndex);
    }

    public Process pollNextProcess() {
        Process nextProcess = peekNextProcess();

        if (nextProcess == null) {
            return null;
        }

        int index = readyQueue.indexOf(nextProcess);

        readyQueue.remove(index);
        enteredTier2At.remove(index);

        return nextProcess;
    }

    public Process startNextProcess() {
        runningProcess = pollNextProcess();
        return runningProcess;
    }

    // Returns true if the process finished in this time unit.
    public boolean runOneTimeUnit(int currentTime) {
        if (runningProcess == null) {
            return false;
        }

        runningProcess.remainingTime--;

        if (runningProcess.remainingTime == 0) {
            runningProcess.finishTime = currentTime + 1;
            runningProcess = null;
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }

    public int size() {
        return readyQueue.size();
    }

    public ArrayList<Process> getWaitingProcesses() {
        return new ArrayList<>(readyQueue);
    }

    // Returns -1 if the process is not waiting in Tier 2.
    public int getWaitingTime(Process process, int currentTime) {
        int index = readyQueue.indexOf(process);

        if (index == -1) {
            return -1;
        }

        return currentTime - enteredTier2At.get(index);
    }

    public void printQueue() {
        if (readyQueue.isEmpty()) {
            System.out.println("Tier 2: [Empty]");
            return;
        }

        System.out.print("Tier 2: [");

        for (int i = 0; i < readyQueue.size(); i++) {
            Process process = readyQueue.get(i);

            System.out.print(
                    process
                    + " (remaining: " + process.remainingTime
                    + ", Priority: " + process.currPriority + ")"
            );

            if (i < readyQueue.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.println("]");
    }
}
