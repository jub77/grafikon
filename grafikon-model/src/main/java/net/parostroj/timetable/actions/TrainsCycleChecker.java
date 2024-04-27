package net.parostroj.timetable.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.model.*;

/**
 * Conflict checker for trains cycle.
 *
 * @author jub
 */
public final class TrainsCycleChecker {

    public enum ConflictType {
        NODE, TIME, SETUP_TIME, TRANSITION_TIME
    }

    public static class Conflict {
        private final TrainsCycleItem from;
        private final TrainsCycleItem to;
        private final Collection<ConflictType> types;

        public Conflict(TrainsCycleItem from, TrainsCycleItem to, Collection<ConflictType> types) {
            this.from = from;
            this.to = to;
            this.types = types;
        }

        public TrainsCycleItem getFrom() {
            return from;
        }

        public TrainsCycleItem getTo() {
            return to;
        }

        public Collection<ConflictType> getType() {
            return types;
        }
    }

    private final static TrainsCycleChecker DRIVER_CHECKER = new TrainsCycleChecker(
            ConflictType.NODE,
            ConflictType.TIME,
            ConflictType.SETUP_TIME,
            ConflictType.TRANSITION_TIME);
    private final static TrainsCycleChecker VEHICLE_CHECKER = new TrainsCycleChecker(
            ConflictType.NODE,
            ConflictType.TIME);

    private final Set<ConflictType> conflictTypes;

    private TrainsCycleChecker(Collection<ConflictType> conflictTypes) {
        this.conflictTypes = Collections.newSetFromMap(new EnumMap<>(ConflictType.class));
        this.conflictTypes.addAll(conflictTypes);
    }

    private TrainsCycleChecker(ConflictType... conflictTypes) {
        this(Arrays.asList(conflictTypes));
    }

    public static TrainsCycleChecker forType(TrainsCycleType type) {
        return type.isDriverType() ? forDriverType() : forVehicleType();
    }

    public static TrainsCycleChecker forDriverType() {
        return DRIVER_CHECKER;
    }

    public static TrainsCycleChecker forVehicleType() {
        return VEHICLE_CHECKER;
    }

    public List<Conflict> checkConflicts(TrainsCycle cycle) {
        List<Conflict> conflicts = new LinkedList<>();
        Iterator<TrainsCycleItem> i = cycle.getItems().iterator();
        TrainsCycleItem last = null;
        TrainsCycleItem first = null;
        if (i.hasNext()) {
            last = i.next();
            first = last;
        }
        while (i.hasNext()) {
            TrainsCycleItem current = i.next();
            checkItems(conflicts, last, current, false);
            last = current;
        }
        if (first != null) {
            if (cycle.isPartOfSequence()) {
                checkItems(conflicts, cycle.getPreviousItemCyclic(first), first, true);
                checkItems(conflicts, last, cycle.getNextItemCyclic(last), true);
            } else {
                checkItems(conflicts, last, first, true);
            }
        }
        return conflicts;
    }

    private void checkItems(List<Conflict> conflicts, TrainsCycleItem first, TrainsCycleItem second, boolean overCycles) {
        List<ConflictType> types = new ArrayList<>();
        if (conflictTypes.contains(ConflictType.NODE) && first.getToInterval().getOwner() != second.getFromInterval().getOwner()) {
            types.add(ConflictType.NODE);
        }
        if (conflictTypes.contains(ConflictType.TIME) && getTimeDifference(first, second, overCycles) < 0) {
            types.add(ConflictType.TIME);
        }
        if (conflictTypes.contains(ConflictType.SETUP_TIME) && second.getSetupTime() != null && getTimeDifference(first, second, overCycles) < second.getSetupTime()) {
            types.add(ConflictType.SETUP_TIME);
        }
        if (conflictTypes.contains(ConflictType.TRANSITION_TIME) && first.getToInterval().getOwner() != second.getFromInterval().getOwner()) {
            Integer transitionTime = getTransitionTime(first.getCycle().getDiagram());
            if (transitionTime != null && getTimeDifference(first, second, overCycles) < transitionTime) {
                types.add(ConflictType.TRANSITION_TIME);
            }
        }
        if (!types.isEmpty()) conflicts.add(new Conflict(first, second, types));
    }

    private int getTimeDifference(TrainsCycleItem first, TrainsCycleItem second, boolean overCycles) {
        Interval firstInterval = getNormalizedIntervalFromItem(first);
        Interval secondInterval = getNormalizedIntervalFromItem(second);
        if (overCycles) {
            firstInterval = getShiftedInterval(firstInterval);
        }
        return secondInterval.getStart() - firstInterval.getEnd();
    }

    private Interval getNormalizedIntervalFromItem(TrainsCycleItem item) {
        return IntervalFactory.createInterval(item.getStartTime(), item.getEndTime()).normalize();
    }

    private Interval getShiftedInterval(Interval interval) {
        return IntervalFactory.createInterval(interval.getStart() - TimeInterval.DAY, interval.getEnd() - TimeInterval.DAY);
    }

    private Integer getTransitionTime(TrainDiagram diagram) {
        Integer okDifference = diagram.getAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, Integer.class);
        if (okDifference != null) {
            // computed difference in model seconds
            okDifference = (int) (okDifference
                    * diagram.getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class) * 60);
        }
        return okDifference;
    }
}
