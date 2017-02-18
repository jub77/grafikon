package net.parostroj.timetable.model.freight;

import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;

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
