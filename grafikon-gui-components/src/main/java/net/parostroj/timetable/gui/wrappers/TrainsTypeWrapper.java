package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TrainType;

/**
 * Train type wrapper.
 *
 * @author jub
 */
public class TrainsTypeWrapper extends Wrapper<TrainType> {

    public TrainsTypeWrapper(TrainType trainType) {
        super(trainType);
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
        return toString(getElement());
    }

    public static String toString(TrainType type) {
        return type.getDesc();
    }
}
