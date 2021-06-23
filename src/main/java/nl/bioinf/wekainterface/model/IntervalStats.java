package nl.bioinf.wekainterface.model;

/**
 * Small class used for keeping track of numbers used in the discretization of numeric attributes.
 * @author jelle
 */
public class IntervalStats {
    private final double numGroups;
    private final int numDecimals;
    private final double groupInterval;
    private double intervalStart;

    public IntervalStats(double numGroups, int numDecimals, double groupInterval, double intervalStart) {
        this.numGroups = numGroups;
        this.numDecimals = numDecimals;
        this.groupInterval = groupInterval;
        this.intervalStart = intervalStart;
    }

    public double getNumGroups() {
        return numGroups;
    }

    public int getNumDecimals() {
        return numDecimals;
    }

    public double getGroupInterval() {
        return groupInterval;
    }

    public double getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(double intervalStart) {
        this.intervalStart = intervalStart;
    }
}
