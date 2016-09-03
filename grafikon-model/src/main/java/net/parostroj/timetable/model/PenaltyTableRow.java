package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.Event;

/**
 * Penalty table row.
 *
 * @author jub
 */
public class PenaltyTableRow {

    public static final PenaltyTableRow ZERO_ROW = new PenaltyTableRow(null, 0, 0, 0);

    private final TrainTypeCategory category;
    private final int speed;
    private int deceleration;
    private int acceleration;

    PenaltyTableRow(TrainTypeCategory category, int speed, int acceleration, int deceleration) {
        this.category = category;
        this.speed = speed;
        this.deceleration = deceleration;
        this.acceleration = acceleration;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        if (this.acceleration != acceleration) {
            this.acceleration = acceleration;
            category.fireEvent(new Event(category, this, new AttributeChange("penalty.info", null, null)));
        }
    }

    public int getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(int deceleration) {
        if (this.deceleration != deceleration) {
            this.deceleration = deceleration;
            category.fireEvent(new Event(category, this, new AttributeChange("penalty.info", null, null)));
        }
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return String.format("<%s,%d,%d>", getSpeed(), getAcceleration(), getDeceleration());
    }
}
