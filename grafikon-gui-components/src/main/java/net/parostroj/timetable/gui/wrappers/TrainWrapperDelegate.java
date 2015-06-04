package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Delegate for trains.
 *
 * @author jub
 */
public class TrainWrapperDelegate implements WrapperDelegate<Train> {

    public enum Type {
        NAME, NAME_AND_END_NODES, NAME_AND_END_NODES_WITH_TIME, NAME_AND_END_NODES_WITH_TIME_TWO_LINES;
    }

    private final Type type;
    private final TrainComparator comparator;

    public TrainWrapperDelegate(Type type, TrainDiagram diagram) {
        this.type = type;
        this.comparator = new TrainComparator(
                TrainComparator.Type.ASC,
                diagram.getTrainsData().getTrainSortPattern());
    }

    public TrainWrapperDelegate(Type type, TrainComparator comparator) {
        this.type = type;
        this.comparator = comparator;
    }

    @Override
    public String toString(Train element) {
        return toStringTrain(element);
    }

    @Override
    public int compare(Train o1, Train o2) {
        return comparator.compare(o1, o2);
    }

    private String toStringTrain(Train train) {
        switch (type) {
            case NAME:
                return train.getName();
            case NAME_AND_END_NODES:
                return String.format("%s (%s,%s)",
                        train.getName(),
                        train.getStartNode(),
                        train.getEndNode());
            case NAME_AND_END_NODES_WITH_TIME: case NAME_AND_END_NODES_WITH_TIME_TWO_LINES:
                String format = type == Type.NAME_AND_END_NODES_WITH_TIME ?
                        "%s (%s[%s],%s[%s])" : "%s%n%s[%s]-%s[%s]";
                return String.format(format,
                        train.getName(),
                        train.getStartNode().getName(),
                        train.getDiagram().getTimeConverter().convertIntToText(train.getStartTime()),
                        train.getEndNode().getName(),
                        train.getDiagram().getTimeConverter().convertIntToText(train.getEndTime()));
            default:
                return train.getName();
        }
    }
}
