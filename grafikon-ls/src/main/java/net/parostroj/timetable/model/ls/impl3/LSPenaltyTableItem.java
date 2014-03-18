package net.parostroj.timetable.model.ls.impl3;

/**
 * Penalty table item.
 *
 * @author jub
 */
public class LSPenaltyTableItem {

    private int lowerLimit;
    private int upperLimit;
    private LSSBType type;
    private int brakingPenalty;
    private int speedingPenalty;

    /**
     * @return the brakingPenalty
     */
    public int getBrakingPenalty() {
        return brakingPenalty;
    }

    /**
     * @param brakingPenalty the brakingPenalty to set
     */
    public void setBrakingPenalty(int brakingPenalty) {
        this.brakingPenalty = brakingPenalty;
    }

    /**
     * @return the lowerLimit
     */
    public int getLowerLimit() {
        return lowerLimit;
    }

    /**
     * @param lowerLimit the lowerLimit to set
     */
    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * @return the speedingPenalty
     */
    public int getSpeedingPenalty() {
        return speedingPenalty;
    }

    /**
     * @param speedingPenalty the speedingPenalty to set
     */
    public void setSpeedingPenalty(int speedingPenalty) {
        this.speedingPenalty = speedingPenalty;
    }

    /**
     * @return the trainType
     */
    public LSSBType getType() {
        return type;
    }

    /**
     * @param trainType the trainType to set
     */
    public void setType(LSSBType trainType) {
        this.type = trainType;
    }

    /**
     * @return the upperLimit
     */
    public int getUpperLimit() {
        return upperLimit;
    }

    /**
     * @param upperLimit the upperLimit to set
     */
    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * @param lowerLimit
     * @param upperLimit
     * @param type
     * @param brakingPenalty
     * @param speedingPenalty
     */
    public LSPenaltyTableItem(int lowerLimit, int upperLimit, LSSBType type, int brakingPenalty, int speedingPenalty) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.type = type;
        this.brakingPenalty = brakingPenalty;
        this.speedingPenalty = speedingPenalty;
    }

    /**
     * Default constructor.
     */
    public LSPenaltyTableItem() {
    }

    @Override
    public String toString() {
        return "<" + lowerLimit + "," + upperLimit + "," + type + "," + speedingPenalty + "," + brakingPenalty + ">";
    }
}