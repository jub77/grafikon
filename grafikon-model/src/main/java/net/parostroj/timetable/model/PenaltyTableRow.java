package net.parostroj.timetable.model;

/**
 * Penalty table row.
 *
 * @author jub
 */
public class PenaltyTableRow {

    public static final PenaltyTableRow ZERO_ROW = new PenaltyTableRow(0, 0, 0);

    private int speed;
    private int deceleration;
    private int acceleration;

    public PenaltyTableRow(int speed, int acceleration, int deceleration) {
        this.speed = speed;
        this.deceleration = deceleration;
        this.acceleration = acceleration;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public int getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(int deceleration) {
        this.deceleration = deceleration;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return String.format("<%s,%d,%d>", getSpeed(), getAcceleration(), getDeceleration());
    }
}
