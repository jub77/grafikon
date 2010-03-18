/*
 * TimeIntervalDirection.java
 * 
 * Created on 5.9.2007, 7:59:25
 */
package net.parostroj.timetable.model;

/**
 * Direction of time interval of the line regarding the direction of the line.
 * 
 * @author jub
 */
public enum TimeIntervalDirection {

    FORWARD(true, 1), BACKWARD(false, -1);

    private boolean forward;
    private int numerical;

    private TimeIntervalDirection(boolean forward, int numerical) {
        this.forward = forward;
        this.numerical = numerical;
    }

    /**
     * @return forward boolean value for this enum
     */
    public boolean isForward() {
        return forward;
    }

    /**
     * @return numerical value of the direction
     */
    public int getNumerical() {
        return numerical;
    }

    /**
     * converts boolean value to this enum.
     * 
     * @param forward direction
     * @return direction enum
     */
    public static TimeIntervalDirection toTimeIntervalDirection(boolean forward) {
        return forward ? FORWARD : BACKWARD;
    }

    /**
     * converts numerical value to this enum.
     * 
     * @param numerical numerical direction
     * @return direction enum
     */
    public static TimeIntervalDirection toTimeIntervalDirection(int numerical) {
        if (numerical == 0) {
            return null;
        }
        return (numerical < 0) ? BACKWARD : FORWARD;
    }
}
