package net.parostroj.timetable.actions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Conflict checker for trains cycle.
 *
 * @author jub
 */
public class TrainsCycleChecker {

    public enum CheckerType {
        NORMAL, SETUP_TIME
    }

    public enum ConflictType {
        UNDEFINED
    }

    public static class Conflict {
        private final TrainsCycleItem from;
        private final TrainsCycleItem to;
        private final ConflictType type;

        public Conflict(TrainsCycleItem from, TrainsCycleItem to, ConflictType type) {
            this.from = from;
            this.to = to;
            this.type = type;
        }

        public TrainsCycleItem getFrom() {
            return from;
        }

        public TrainsCycleItem getTo() {
            return to;
        }

        public ConflictType getType() {
            return type;
        }
    }

    public TrainsCycleChecker() {
    }

    public List<Conflict> checkConflicts(TrainsCycle cycle) {
        List<Conflict> conflicts = new LinkedList<Conflict>();
        Iterator<TrainsCycleItem> i = cycle.getItems().iterator();
        TrainsCycleItem last = null;
        if (i.hasNext()) {
            last = i.next();
        }
        while (i.hasNext()) {
            TrainsCycleItem current = i.next();
            if (last.getToInterval().getOwner() != current.getFromInterval().getOwner() || last.getNormalizedEndTime() >= current.getNormalizedStartTime()) {
                conflicts.add(new Conflict(last, current, ConflictType.UNDEFINED));
            }
            last = current;
        }
        return conflicts;
    }
}
