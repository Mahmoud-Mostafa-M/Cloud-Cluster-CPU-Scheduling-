public class Process {
    public int id, arrivalTime, burstTime, iniPriority, currPriority, remainingTime, startTime, finishTime, tier2EntryTime;

    public Process(int id, int arrivalTime, int burstTime, int iniPriority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.iniPriority = iniPriority;
        this.currPriority = iniPriority;
        this.remainingTime = burstTime;
        this.startTime = -1;
        this.finishTime = -1;
        this.tier2EntryTime = -1;
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}