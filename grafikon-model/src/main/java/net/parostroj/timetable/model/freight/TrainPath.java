package net.parostroj.timetable.model.freight;

import java.util.List;

import net.parostroj.timetable.utils.TimeUtil;

/**
 * List of train connections.
 *
 * @author jub
 */
public interface TrainPath extends List<TrainConnection> {

    default int getStartTime() {
        if (isEmpty()) { throw new IllegalStateException("Path is empty"); }
        return get(0).getStartTime();
    }

    default int getEndTime() {
        if (isEmpty()) { throw new IllegalStateException("Path is empty"); }
        return get(size() - 1).getStartTime();
    }

    default int getLength() {
        return isEmpty() ? 0 : TimeUtil.difference(getStartTime(), getEndTime());
    }
}
