package net.parostroj.timetable.filters;

import java.util.Set;

import com.google.common.base.Predicate;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;

/**
 * Class for filtering train according to types.
 *
 * @author jub
 */
public abstract class TrainTypePredicate implements Predicate<Train> {

    public static enum PredefinedType {
        FREIGHT("freight"), PASSENGER("passenger");

        private final String key;

        private PredefinedType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public static TrainTypePredicate getTrainsByType(PredefinedType type) {
        switch (type) {
            case FREIGHT:
                return new TrainTypePredicate() {
                    @Override
                    public boolean apply(Train train) {
                        TrainTypeCategory category = train.getType() != null ? train.getType().getCategory() : null;
                        return category != null ? category.getKey().equals(PredefinedType.FREIGHT.getKey()) : false;
                    }
                };
            case PASSENGER:
                return new TrainTypePredicate() {
                    @Override
                    public boolean apply(Train train) {
                        TrainTypeCategory category = train.getType() != null ? train.getType().getCategory() : null;
                        return category != null ? category.getKey().equals(PredefinedType.PASSENGER.getKey()) : false;
                    }
                };
            default:
                throw new IllegalArgumentException("Unexpected type: " + type);
        }
    }

    public static TrainTypePredicate getTrainsByType(final Set<TrainType> types) {
        return new TrainTypePredicate() {
            @Override
            public boolean apply(Train train) {
                return types.contains(train.getType());
            }
        };
    }
}
