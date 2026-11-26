// Tier 3: Aging, to stop low-priority processes from starving in Tier 2.
// effectivePriority = initialPriority - (timeWaited / AGING_INTERVAL)
public class Tier3 {

    public static final int AGING_INTERVAL = 4;
    public static final int MIN_PRIORITY = 1;

    public void applyAging(Tier2 tier2, int currentTime) {
        for (Process p : tier2.getWaitingProcesses()) {
            int waited = tier2.getWaitingTime(p, currentTime);
            int boost = waited / AGING_INTERVAL;

            int newPriority = p.iniPriority - boost;
            if (newPriority < MIN_PRIORITY) {
                newPriority = MIN_PRIORITY;
            }
            p.currPriority = newPriority;

            printAging(p, waited, boost, currentTime);
        }
    }

    private void printAging(Process p, int waited, int boost, int currentTime) {
        String line = "  " + p + ": Waited from T=" + (currentTime - waited) + " to T=" + currentTime
                + " (" + waited + " units).";

        if (boost == 0) {
            line += " Effective Priority = " + p.currPriority;
        } else {
            line += " Aging: " + waited + "/" + AGING_INTERVAL + " = " + boost
                    + ". Effective Priority = " + p.iniPriority + " - " + boost + " = " + (p.iniPriority - boost);
            if (p.iniPriority - boost < MIN_PRIORITY) {
                line += " -> kept at minimum " + MIN_PRIORITY;
            }
        }
        System.out.println(line);
    }
}
