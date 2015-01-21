package net.parostroj.timetable.filters;

import net.parostroj.timetable.actions.FreightHelper;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.Train;

import com.google.common.base.Predicate;

public class ModelPredicates {

    public static Predicate<Train> managedTrain() {
        return train -> FreightHelper.isManaged(train);
    }

    public static <T extends AttributesHolder> Predicate<T> inGroup(final Group group) {
        return holder -> {
            Group foundGroup = holder.getAttributes().get("group", Group.class);
            if (group == null) {
                return foundGroup == null;
            } else {
                return group.equals(foundGroup);
            }
        };
    }
}
