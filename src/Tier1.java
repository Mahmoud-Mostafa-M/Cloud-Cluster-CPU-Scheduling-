import java.util.ArrayList;

// Tier 1: Round Robin queue. Every new process starts here and gets one quantum.
public class Tier1 {

    private final ArrayList<Process> readyQueue = new ArrayList<>();

    public void addProcess(Process process) {
        readyQueue.add(process);
    }

    public Process pollNextProcess() {
        if (readyQueue.isEmpty()) {
            return null;
        }
        return readyQueue.remove(0);
    }

    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }

    public void printQueue() {
        if (readyQueue.isEmpty()) {
            System.out.println("Tier 1: [Empty]");
            return;
        }

        System.out.print("Tier 1: [");

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
