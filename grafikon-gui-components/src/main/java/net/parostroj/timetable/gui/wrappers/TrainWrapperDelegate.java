package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Delegate for trains.
 *
 * @author jub
 */
public class TrainWrapperDelegate implements WrapperDelegate {

    public enum Type {
        NAME, NAME_AND_END_NODES, NAME_AND_END_NODES_WITH_TIME;
    }

    private Type type;
    private TrainComparator comparator;

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
    public String toString(Object element) {
        return toStringTrain((Train) element);
    }

    @Override
    public int compare(Object o1, Object o2) {
        return comparator.compare((Train) o1, (Train) o2);
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
            case NAME_AND_END_NODES_WITH_TIME:
                return String.format("%s (%s[%s],%s[%s])",
                        train.getName(),
                        train.getStartNode().getName(),
                        train.getTrainDiagram().getTimeConverter().convertFromIntToText(train.getStartTime()),
                        train.getEndNode().getName(),
                        train.getTrainDiagram().getTimeConverter().convertFromIntToText(train.getEndTime()));
            default:
                return train.getName();
        }
    }
}
