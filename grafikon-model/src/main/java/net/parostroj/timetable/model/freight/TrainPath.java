package net.parostroj.timetable.model.freight;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * List of train connections.
 *
 * @author jub
 */
public interface TrainPath extends List<TrainConnection> {

    default int getStartTime() {
        if (isEmpty()) { throw new NoSuchElementException("Path is empty"); }
        return getFirst().getStartTime();
    }

    default int getEndTime() {
        if (isEmpty()) { throw new NoSuchElementException("Path is empty"); }
        return getLast().getEndTime();
    }

    default int getLength() {
        return isEmpty() ? 0 : TimeUtil.difference(getStartTime(), getEndTime());
    }

    default TrainConnection getFirst() {
        if (isEmpty()) { throw new NoSuchElementException("Path is empty"); }
        return get(0);
    }

    default TrainConnection getLast() {
        if (isEmpty()) { throw new NoSuchElementException("Path is empty"); }
        return get(size() - 1);
    }

    /**
     * @return true if at the destination the direction of the train is in reverse (if the
     *      next station is in other direction)
     */
    default boolean isDirectionReversed() {
        boolean reverse = false;
        TimeInterval lastInterval = null;
        for (TrainConnection tConn : this) {
            // change from previous connection
            if (lastInterval != null
                    && lastInterval.getFromTrackConnector().get().getOrientation() == tConn
                            .getFrom().getToTrackConnector().get().getOrientation()) {
                reverse = !reverse;
            }

            Iterator<TimeInterval> i = tConn.getTrain().getTimeIntervalList().iterator();
            while (i.hasNext() && i.next() != tConn.getFrom()) {
                // skip to relevant intervals
            }

            // check open interval (end points are handled differently)
            TimeInterval currentInterval;
            while (i.hasNext() && (currentInterval = i.next()) != tConn.getTo()) {
                if (currentInterval.isLineOwner()) {
                    continue;
                }
                if (currentInterval.isDirectionChange()) {
                    reverse = !reverse;
                }
            }
            lastInterval = tConn.getTo();
        }
        return reverse;
    }

    static TrainPath empty() {
        class EmptyPath extends AbstractList<TrainConnection> implements TrainPath {
            @Override
            public TrainConnection get(int index) {
                throw new ArrayIndexOutOfBoundsException();
            }

            @Override
            public int size() {
                return 0;
            }

        }
        return new EmptyPath();
    }
}
