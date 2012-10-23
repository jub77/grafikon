package net.parostroj.timetable.gui.views;

import java.util.Set;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.utils.Filter;

/**
 * Class for filtering train according to some criteria.
 *
 * @author jub
 */
public abstract class TrainFilter implements Filter<Train, Train> {

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

    public static TrainFilter getTrainFilter(PredefinedType type) {
        switch (type) {
            case FREIGHT:
                return new TrainFilter() {
                    @Override
                    public boolean is(Train train) {
                        return train.getType().getCategory().getKey().equals(PredefinedType.FREIGHT.getKey());
                    }

                    @Override
                    public Train get(Train train) {
                        return train;
                    }
                };
            case PASSENGER:
                return new TrainFilter() {
                    @Override
                    public boolean is(Train train) {
                        return train.getType().getCategory().getKey().equals(PredefinedType.PASSENGER.getKey());
                    }

                    @Override
                    public Train get(Train train) {
                        return train;
                    }
                };
            default:
                throw new IllegalArgumentException("Unexpected type: " + type);
        }
    }

    public static TrainFilter getTrainFilter(final Set<TrainType> types) {
        return new TrainFilter() {
            @Override
            public boolean is(Train train) {
                return types.contains(train.getType());
            }

            @Override
            public Train get(Train train) {
                return train;
            }
        };
    }
}
