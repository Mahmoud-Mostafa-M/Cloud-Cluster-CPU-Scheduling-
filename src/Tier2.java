import java.util.ArrayList;

// Tier 2: Non-Preemptive Priority queue for processes demoted from Tier 1.
public class Tier2 {

    // Insertion order is kept, so equal priorities are served first come, first served.
    private final ArrayList<Process> readyQueue = new ArrayList<>();

    public void addProcess(Process process, int currentTime) {
        if (process == null) {
            return;
        }

        if (readyQueue.contains(process)) {
            return;
        }

        process.tier2EntryTime = currentTime;
        readyQueue.add(process);
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

        if (nextProcess != null) {
            readyQueue.remove(nextProcess);
        }

        return nextProcess;
    }

    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }

    public ArrayList<Process> getWaitingProcesses() {
        return new ArrayList<>(readyQueue);
    }

    // Returns -1 if the process is not waiting in Tier 2.
    public int getWaitingTime(Process process, int currentTime) {
        if (!readyQueue.contains(process)) {
            return -1;
        }

        return currentTime - process.tier2EntryTime;
    }
}
