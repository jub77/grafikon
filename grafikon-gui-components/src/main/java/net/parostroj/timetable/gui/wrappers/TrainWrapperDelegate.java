package net.parostroj.timetable.gui.wrappers;

import java.util.Comparator;

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
        this.comparator = new TrainComparator(diagram.getTrainsData().getTrainSortPattern());
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
    public Comparator<? super Train> getComparator() {
        return comparator;
    }

    private String toStringTrain(Train train) {
        switch (type) {
            case NAME:
                return train.getDefaultName();
            case NAME_AND_END_NODES:
                return String.format("%s (%s,%s)",
                        train.getDefaultName(),
                        train.getStartNode(),
                        train.getEndNode());
            case NAME_AND_END_NODES_WITH_TIME: case NAME_AND_END_NODES_WITH_TIME_TWO_LINES:
                String format = type == Type.NAME_AND_END_NODES_WITH_TIME ?
                        "%s (%s[%s],%s[%s])" : "%s%n%s[%s]-%s[%s]";
                return String.format(format,
                        train.getDefaultName(),
                        train.getStartNode().getName(),
                        train.getDiagram().getTimeConverter().convertIntToText(train.getStartTime()),
                        train.getEndNode().getName(),
                        train.getDiagram().getTimeConverter().convertIntToText(train.getEndTime()));
            default:
                return train.getDefaultName();
        }
    }
}
