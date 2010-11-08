package net.parostroj.timetable.gui.helpers;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Wrapper for time interval.
 *
 * @author jub
 */
public class TimeIntervalWrapper extends Wrapper<TimeInterval> {

    public TimeIntervalWrapper(TimeInterval element) {
        super(element);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return getElement().getOwner().toString();
    }
}
