import java.util.ArrayList;

public class Tier2 {

    // Processes that were demoted from Tier 1.
    // The list keeps insertion order, which we use when priorities are equal.
    private final ArrayList<Process> readyQueue = new ArrayList<>();

    // Stores the time at which each process entered Tier 2.
    // Tier 3 can use this to calculate how long a process has been waiting.
    private final ArrayList<Integer> enteredTier2At = new ArrayList<>();

    // Add a process to Tier 2 when it uses its whole Tier 1 quantum.
    public void addProcess(Process process, int currentTime) {
        if (process == null) {
            return;
        }

        // Prevent accidentally adding the same process twice.
        if (readyQueue.contains(process)) {
            return;
        }

        readyQueue.add(process);
        enteredTier2At.add(currentTime);
    }

    // Return the waiting process with the highest priority.
    // In this assignment, a smaller priority number means higher priority.
    // If priorities are equal, the process that entered Tier 2 first wins.
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

    // Remove and return the next process selected by priority.
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

    // Check whether Tier 2 has no waiting processes.
    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }

    // Number of processes currently waiting in Tier 2.
    public int size() {
        return readyQueue.size();
    }

    // Return a copy so other classes can inspect the queue safely.
    // Tier 3 may update currPriority on the Process objects in this list.
    public ArrayList<Process> getWaitingProcesses() {
        return new ArrayList<>(readyQueue);
    }

    // How long this process has been waiting in Tier 2.
    // Returns -1 if the process is not currently waiting in this queue.
    public int getWaitingTime(Process process, int currentTime) {
        int index = readyQueue.indexOf(process);

        if (index == -1) {
            return -1;
        }

        return currentTime - enteredTier2At.get(index);
    }

    // Print the current waiting queue for the scheduling timeline.
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
                    + "(remaining: " + process.remainingTime
                    + ", Priority: " + process.currPriority + ")"
            );

            if (i < readyQueue.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.println("]");
    }
}
