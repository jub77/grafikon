package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TrainTypeCategory;

/**
 * Train type category wrapper.
 *
 * @author jub
 */
public class TrainsTypeCategoryWrapper extends Wrapper<TrainTypeCategory> {

    public TrainsTypeCategoryWrapper(TrainTypeCategory trainTypeCategory) {
        super(trainTypeCategory);
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

    public static String toString(TrainTypeCategory category) {
        return category.getName();
    }
}
