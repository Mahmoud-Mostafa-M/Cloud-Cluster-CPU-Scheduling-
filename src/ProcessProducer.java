import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
public class ProcessProducer extends Thread {
    private BlockingQueue<Process> queue;
    private Process[] processes;
    public ProcessProducer(BlockingQueue<Process> queue, Process[] processes){
        this.queue = queue;
        this.processes=processes;
    }
    @Override
    public void run(){
        int lastArrival=0;
        int gap;
        try {
            for (Process p : processes) {
                gap = p.arrivalTime - lastArrival;
                Thread.sleep(gap);
                queue.add(p);
                lastArrival = p.arrivalTime;
            }
        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
        }
    }
}
