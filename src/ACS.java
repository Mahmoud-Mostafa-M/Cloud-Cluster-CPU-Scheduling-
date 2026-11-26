import java.util.concurrent.BlockingQueue;
public class ACS {

void schedule(BlockingQueue<Process> q, int totalProcesses,  int quantum, int CS){
    int processed=0;
    Process currentProcess;
while(processed<totalProcesses){
    try {
        currentProcess = q.take();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
int executionTime = Math.min(currentProcess.burstTime, quantum);
try{
    //currentProcess.startTime=System.nanoTime();
Thread.sleep(executionTime + CS);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
currentProcess.remainingTime-=executionTime;
if(currentProcess.remainingTime>0){
// priorityQueue.add(currentProcess);
}
else if(currentProcess.remainingTime==0){
//currentProcess.finishTime=System.nanoTime();
}
processed++;
}
}

}
