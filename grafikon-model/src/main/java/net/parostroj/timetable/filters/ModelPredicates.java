package net.parostroj.timetable.filters;

import java.util.function.Predicate;
import net.parostroj.timetable.model.*;

import java.util.Set;

public final class ModelPredicates {

    private ModelPredicates() {}

    public enum PredefinedTrainTypes {
        FREIGHT("freight"), PASSENGER("passenger");

        private final String key;

        PredefinedTrainTypes(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public static Predicate<Train> managedTrain() {
        return Train::isManagedFreight;
    }

    public static <T extends ObjectWithId> Predicate<T> matchId(final String id) {
        return item -> item.getId().equals(id);
    }

    public static <T extends AttributesHolder> Predicate<T> inGroup(final Group group) {
        return holder -> {
            Group foundGroup = holder.getAttributes().get(Train.ATTR_GROUP, Group.class);
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

    public static Predicate<Train> getTrainsByType(final Set<TrainType> types) {
        return train -> types.contains(train.getType());
    }

    public static Predicate<Train> getTrainsByType(PredefinedTrainTypes types) {
        return switch (types) {
            case FREIGHT -> train -> {
                TrainTypeCategory category = train.getType() != null ? train.getType().getCategory() : null;
                return category != null && category.getKey().equals(PredefinedTrainTypes.FREIGHT.getKey());
            };
            case PASSENGER -> train -> {
                TrainTypeCategory category = train.getType() != null ? train.getType().getCategory() : null;
                return category != null && category.getKey().equals(PredefinedTrainTypes.PASSENGER.getKey());
            };
        };
    }
}
