package net.parostroj.timetable.filters;

import net.parostroj.timetable.actions.FreightHelper;
import net.parostroj.timetable.model.*;

import com.google.common.base.Predicate;

public class ModelPredicates {

    public static Predicate<Train> managedTrain() {
        return FreightHelper::isManaged;
    }

    public static <T extends AttributesHolder> Predicate<T> inGroup(final Group group) {
        return holder -> {
            Group foundGroup = holder.getAttributes().get(TrainAttributes.ATTR_GROUP, Group.class);
            if (group == null) {
                return foundGroup == null;
            } else {
                return group.equals(foundGroup);
            }
        };
    }

    public static boolean nodeInterval(TimeInterval interval) {
        return interval.isNodeOwner();
    }

    public static boolean lineInterval(TimeInterval interval) {
        return interval.isLineOwner();
    }
}
