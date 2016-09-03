package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainTypeCategory;

/**
 * Storage for penalty table row.
 *
 * @author jub
 */
@XmlType(propOrder = {"speed", "acceleration", "deceleration"})
public class LSPenaltyTableRow {

    private int speed;
    private int acceleration;
    private int deceleration;

    public LSPenaltyTableRow() {
    }

    public LSPenaltyTableRow(PenaltyTableRow row) {
        this.speed = row.getSpeed();
        this.acceleration = row.getAcceleration();
        this.deceleration = row.getDeceleration();
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

    public PenaltyTableRow createPenaltyTableRow(TrainTypeCategory category) {
        return category.createPenaltyTableRow(speed, acceleration, deceleration);
    }
}
