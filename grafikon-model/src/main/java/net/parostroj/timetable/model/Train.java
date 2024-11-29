package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.model.computation.TrackSelectionComputation;
import net.parostroj.timetable.model.computation.TrainRouteSelection;
import net.parostroj.timetable.model.computation.TrainRouteTracksComputation;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.ObservableObject;
import net.parostroj.timetable.utils.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

import com.google.common.collect.*;

/**
 * Train.
 *
 * @author jub
 */
public class Train implements AttributesHolder, ObjectWithId, Visitable,
        TrainDiagramPart, Iterable<TimeInterval>, ObservableObject {

    public static final String ATTR_GROUP = "group";
    public static final String ATTR_DIESEL = "diesel";
    public static final String ATTR_ELECTRIC = "electric";
    public static final String ATTR_SHOW_STATION_LENGTH = "show.station.length";
    public static final String ATTR_EMPTY = "empty";
    public static final String ATTR_ROUTE = "route";
    public static final String ATTR_WEIGHT = "weight";
    public static final String ATTR_WEIGHT_LIMIT = "weight.limit";
    public static final String ATTR_NUMBER = "number";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_TOP_SPEED = "topSpeed";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_COMPLETE_NAME = "completeName";
    public static final String ATTR_MANAGED_FREIGHT = "managed.freight";
    public static final String ATTR_OPTIONAL = "optional";
    public static final String ATTR_TECHNOLOGICAL_BEFORE = "technological.before";
    public static final String ATTR_TECHNOLOGICAL_AFTER = "technological.after";
    public static final String ATTR_PREVIOUS_JOINED_TRAIN = "previous.joined.train";
    public static final String ATTR_NEXT_JOINED_TRAIN = "next.joined.train";

    public enum NameType { NORMAL, COMPLETE }

    /** Train diagram reference. */
    private final TrainDiagram diagram;
    /** ID. */
    private final String id;
    /** Train number. */
    private String number;
    /** Description. */
    private String description;
    /** Train type. */
    private TrainType type;
    /** List of time intervals. */
    private final TimeIntervalList intervalList;
    /** Top speed. */
    private Integer topSpeed;
    /** Cycles. */
    private final ListMultimap<TrainsCycleType, TrainsCycleItem> cycles;
    /* Attributes of the train. */
    private final Attributes attributes;
    private final ListenerSupport listenerSupport;
    private boolean attached;

    /* Technological times. */
    private TimeInterval timeBefore;
    private TimeInterval timeAfter;

    private Train previousJoinedTrain;

    /* Cached map for train cycles. */
    private final TrainCachedCycles cachedCycles;
    private final Collection<TimeInterval> nodeIntervalsView;
    private final Collection<TimeInterval> lineIntervalsView;
    private final List<TimeInterval> timeIntervalsView;
    private final TrainNameDelegate nameDelegate;

    /**
     * Constructor.
     *
     * @param id id
     * @param diagram train diagram
     */
    Train(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.nameDelegate = new TrainNameDelegate(this);
        cachedCycles = new TrainCachedCycles();
        intervalList = new TimeIntervalList();
        nodeIntervalsView = Collections.unmodifiableCollection(
                Collections2.filter(intervalList, ModelPredicates::nodeInterval));
        lineIntervalsView = Collections.unmodifiableCollection(
                Collections2.filter(intervalList, ModelPredicates::lineInterval));
        timeIntervalsView = Collections.unmodifiableList(intervalList);
        listenerSupport = new ListenerSupport();
        attributes = new Attributes((attrs, change) ->  {
            refreshCachedNames();
            if (change.checkName(Train.ATTR_WEIGHT_LIMIT)) {
                Train.this.recalculate();
            }
            fixManagedFreight(change);
            fixJoinedTrains(change);
            listenerSupport.fireEvent(new Event(Train.this, change));
        }, (attrs, change) -> {
            if (change.checkName(Train.ATTR_NEXT_JOINED_TRAIN)) {
                Train newTrain = (Train) change.getNewValue();
                return newTrain == null
                        || this.getLastInterval().getOwnerAsNode() == newTrain.getFirstInterval().getOwnerAsNode();
            }
            return true;
        });
        cycles = LinkedListMultimap.create();
        attached = false;
        timeBefore = null;
        timeAfter = null;
        this.setAttribute(ATTR_DIESEL, false);
        this.setAttribute(ATTR_ELECTRIC, false);
    }

    private void fixManagedFreight(AttributeChange change) {
        if (change.checkName(Train.ATTR_MANAGED_FREIGHT) && !Boolean.TRUE.equals(change.getNewValue())) {
            for (TimeInterval interval : intervalList) {
                interval.removeAttribute(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
            }
        }
    }

    private void fixJoinedTrains(AttributeChange change) {
        if (change.checkName(Train.ATTR_NEXT_JOINED_TRAIN)) {
            Train oldTrain = (Train) change.getOldValue();
            Train newTrain = (Train) change.getNewValue();
            if (oldTrain != null) {
                oldTrain.setPreviousJoinedTrain(oldTrain.previousJoinedTrain, null);
            }
            if (newTrain != null) {
                Train newTrainOldPrevious = newTrain.previousJoinedTrain;
                newTrain.previousJoinedTrain = null;
                if (newTrainOldPrevious != null) {
                    newTrainOldPrevious.setNextJoinedTrain(null);
                }
                newTrain.setPreviousJoinedTrain(newTrainOldPrevious, this);
            }
        }
    }

    /**
     * @return id of the train
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return train diagram
     */
    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    /**
     * adds listener to train.
     * @param listener listener
     */
    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener from train.
     * @param listener listener
     */
    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    /**
     * @return number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number number to be set
     */
    public void setNumber(String number) {
        if (!ObjectsUtil.compareWithNull(number, this.number)) {
            String oldNumber = this.number;
            this.number = number;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_NUMBER, oldNumber, number)));
            this.refreshCachedNames();
        }
    }

    /**
     * @return name of the train depending on the pattern
     */
    public String getDefaultName() {
        return nameDelegate.getName();
    }

    /**
     * @return complete name of the train depending on the pattern
     */
    public String getDefaultCompleteName() {
        return nameDelegate.getCompleteName();
    }

    public TranslatedString getName() {
        return this.getName(NameType.NORMAL);
    }

    public TranslatedString getCompleteName() {
        return this.getName(NameType.COMPLETE);
    }

    /**
     * @param nameType type of the name
     * @return translated name of specified type
     */
    public TranslatedString getName(NameType nameType) {
        return nameDelegate.getName(nameType);
    }

    /**
     * clears cached train names.
     */
    public void refreshCachedNames() {
        nameDelegate.refreshCachedNames();
    }

    /**
     * @return description of the train
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description sets description
     */
    public void setDescription(String description) {
        if (!ObjectsUtil.compareWithNull(description, this.description)) {
            String oldDesc = this.description;
            this.description = description;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_DESCRIPTION, oldDesc,
                    description)));
            this.refreshCachedNames();
        }
    }

    /**
     * @return top speed
     */
    public Integer getTopSpeed() {
        return topSpeed;
    }

    /**
     * @param topSpeed top speed to be set
     */
    public void setTopSpeed(Integer topSpeed) {
        if (topSpeed != null && topSpeed <= 0) {
            throw new IllegalArgumentException("Speed cannot be 0 or less");
        }
        if (!Objects.equals(this.topSpeed, topSpeed)) {
            Integer oldSpeed = this.topSpeed;
            this.topSpeed = topSpeed;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TOP_SPEED, oldSpeed, topSpeed)));
            this.recalculate();
        }
    }

    @Override
    public String toString() {
        return getDefaultName();
    }

    /**
     * @return the type
     */
    public TrainType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TrainType type) {
        if (!ObjectsUtil.compareWithNull(type, this.type)) {
            TrainType oldType = this.type;
            this.type = type;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TYPE, oldType, type)));
            this.refreshCachedNames();
            // penalties can be changed
            this.recalculate();
        }
    }

    /**
     * @return the intervals
     */
    public List<TimeInterval> getTimeIntervalList() {
        return timeIntervalsView;
    }

    public Collection<TimeInterval> getNodeIntervals() {
        return nodeIntervalsView;
    }

    public Collection<TimeInterval> getLineIntervals() {
        return lineIntervalsView;
    }

    protected TimeIntervalList getIntervalList() {
        return intervalList;
    }

    /**
     * @return map with trains cycle items
     */
    public ListMultimap<TrainsCycleType, TrainsCycleItem> getCyclesMap() {
        return Multimaps.unmodifiableListMultimap(cycles);
    }

    /**
     * @param type type of the cycles
     * @return list with trains cycle items of specified type (list cannot be modified)
     */
    public List<TrainsCycleItem> getCycles(TrainsCycleType type) {
        return Collections.unmodifiableList(cycles.get(type));
    }

    /**
     * @param item train cycle item to be added
     */
    protected void addCycleItem(TrainsCycleItem item) {
        TrainsCycleType cycleType = item.getCycle().getType();
        cachedCycles.addCycleItem(intervalList, cycles.get(cycleType), item, true);
        cachedCycles.add(intervalList, item);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.ADDED, item));
        this.checkRecalculateCycle(item.getCycle());
    }

    /**
     * @param item train cycle item to be removed
     */
    protected void removeCycleItem(TrainsCycleItem item) {
        TrainsCycleType cycleType = item.getCycle().getType();
        this.cycles.remove(cycleType, item);
        cachedCycles.remove(item);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.REMOVED, item));
        this.checkRecalculateCycle(item.getCycle());
    }

    protected void replaceCycleItem(TrainsCycleItem newItem, TrainsCycleItem oldItem) {
        TrainsCycleType cycleType = oldItem.getCycle().getType();
        this.cycles.remove(cycleType, oldItem);
        cachedCycles.remove(oldItem);
        cachedCycles.addCycleItem(intervalList, cycles.get(cycleType), newItem, true);
        cachedCycles.add(intervalList, newItem);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.REPLACED, newItem, ListData.createData(oldItem, newItem)));
        this.checkRecalculateCycle(newItem.getCycle());
    }

    private void checkRecalculateCycle(TrainsCycle cycle) {
        if (TrainsCycleType.isEngineType(cycle.getType()) && cycle.getAttributes().containsKey(TrainsCycle.ATTR_ENGINE_CLASS)) {
            this.recalculate();
        }
    }

    /**
     * @param type trains cycle type
     * @param interval time interval
     * @return list of train cycle items that covers given interval (empty list if there are none)
     */
    public Collection<TrainsCycleItem> getCycleItemsForInterval(TrainsCycleType type, TimeInterval interval) {
        return cachedCycles.get(interval, type);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * @return <code>true</code> if there are overlapping intervals for the train
     */
    public boolean isConflicting() {
        for (TimeInterval interval : intervalList) {
            if (interval.isOverlapping()) {
                return true;
            }
        }
        // test technological times
        return ((timeBefore != null && timeBefore.isOverlapping()) ||
                (timeAfter != null && timeAfter.isOverlapping()));
    }

    /**
     * @return set of train with conflicts with this train
     */
    public Set<Train> getConflictingTrains() {
        ReferenceHolder<Set<Train>> conflictsRef = new ReferenceHolder<>();
        for (TimeInterval interval : intervalList) {
            this.addOverlappingTrains(interval, conflictsRef);
        }
        // test technologic intervals
        this.addOverlappingTrains(timeBefore, conflictsRef);
        this.addOverlappingTrains(timeAfter, conflictsRef);
        // return result
        if (conflictsRef.get() == null) {
            return Collections.emptySet();
        } else {
            return conflictsRef.get();
        }
    }

    /**
     * adds conflicting trains into the set
     * @param interval time interval
     * @param conflictsRef set of trains
     */
    private void addOverlappingTrains(TimeInterval interval, ReferenceHolder<Set<Train>> conflictsRef) {
        if (interval != null && interval.isOverlapping()) {
            for (TimeInterval i2 : interval.getOverlappingIntervals()) {
                if (conflictsRef.get() == null) {
                    conflictsRef.set(new HashSet<>());
                }
                conflictsRef.get().add(i2.getTrain());
            }
        }
    }

    /**
     * @return <code>true</code> if all stops that needs platform have one
     */
    public boolean checkPlatforms() {
        for (TimeInterval interval : intervalList) {
            if (!interval.isPlatformOk()) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns first node of the train timetable.
     *
     * @return first node
     */
    public Node getStartNode() {
        return intervalList.get(0).getOwnerAsNode();
    }

    /**
     * returns last node of the train.
     *
     * @return last node
     */
    public Node getEndNode() {
        return intervalList.get(intervalList.size() - 1).getOwnerAsNode();
    }

    /**
     * returns start time of the train.
     *
     * @return start time
     */
    public int getStartTime() {
        return intervalList.get(0).getStart();
    }

    /**
     * returns end time of the train.
     *
     * @return end time
     */
    public int getEndTime() {
        return intervalList.get(intervalList.size() - 1).getEnd();
    }

    /**
     * @param length technological time before train start
     */
    public void setTimeBefore(int length) {
        boolean fireEvent = true;
        int oldLength = this.getTimeBefore();
        if (length == 0 && timeBefore != null) {
            if (isAttached()) {
                timeBefore.removeFromOwner();
            }
            timeBefore = null;
        } else if (length != 0 && timeBefore == null) {
            TimeInterval firstInterval = this.getFirstInterval();
            timeBefore = new TimeInterval(IdGenerator.getInstance().getId(), this, firstInterval.getOwner(),
                    firstInterval.getStart() - length + 1, firstInterval.getStart() - 1,
                    firstInterval.getTrack());
            if (isAttached()) {
                timeBefore.addToOwnerWithoutCheck();
            }
        } else if (length != 0) {
            TimeInterval firstInterval = this.getFirstInterval();
            // recalculate time
            int start = firstInterval.getStart() - length + 1;
            int end = start + length - 2;
            timeBefore.setInterval(start, end);
            timeBefore.setTrack(firstInterval.getTrack());
            fireEvent = oldLength != length;
            if (isAttached()) {
                timeBefore.updateInOwner();
            }
        } else {
            fireEvent = false;
        }
        if (fireEvent) {
            listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TECHNOLOGICAL_BEFORE, oldLength, length)));
        }
    }

    /**
     * @param length technological time after train end
     */
    public void setTimeAfter(int length) {
        boolean fireEvent = true;
        int oldLength = this.getTimeAfter();
        if (length == 0 && timeAfter != null) {
            if (isAttached()) {
                timeAfter.removeFromOwner();
            }
            timeAfter = null;
        } else if (length != 0 && timeAfter == null) {
            TimeInterval lastInterval = this.getLastInterval();
            timeAfter = new TimeInterval(IdGenerator.getInstance().getId(), this, lastInterval.getOwner(),
                    lastInterval.getEnd() + 1, lastInterval.getEnd() + length - 1,
                    lastInterval.getTrack());
            if (isAttached()) {
                timeAfter.addToOwnerWithoutCheck();
            }
        } else if (length != 0) {
            TimeInterval lastInterval = this.getLastInterval();
            // recalculate time
            int start = lastInterval.getEnd() + 1;
            int end = start + length - 2;
            timeAfter.setInterval(start, end);
            timeAfter.setTrack(lastInterval.getTrack());
            fireEvent = oldLength != length;
            if (isAttached()) {
                timeAfter.updateInOwner();
            }
        } else {
            fireEvent = false;
        }
        if (fireEvent) {
            listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TECHNOLOGICAL_AFTER, oldLength, length)));
        }
    }

    /**
     * @return technological time before train start
     */
    public int getTimeBefore() {
        return (timeBefore == null) ? 0 : timeBefore.getLength() + 2;
    }

    /**
     * @return technological time after train end
     */
    public int getTimeAfter() {
        return (timeAfter == null) ? 0 : timeAfter.getLength() + 2;
    }

    /**
     * @return technological time interval before train start
     */
    public TimeInterval getTimeIntervalBefore() {
        return timeBefore;
    }

    /**
     * @return technological time interval after train end
     */
    public TimeInterval getTimeIntervalAfter() {
        return timeAfter;
    }

    /**
     * updates technological times.
     */
    private void updateTechnologicalTimes() {
        this.updateTechnologicalTimeBefore();
        this.updateTechnologicalTimeAfter();
    }

    private void updateTechnologicalTimeAfter() {
        this.setTimeAfter(this.getTimeAfter());
    }

    private void updateTechnologicalTimeBefore() {
        this.setTimeBefore(this.getTimeBefore());
    }

    /**
     * moves train to the new starting time.
     *
     * @param time starting time
     */
    public void move(int time) {
        if (time == intervalList.get(0).getStart()) {
            return;
        }
        intervalList.get(0).move(time);
    	this.recalculateImpl(0);
        this.listenerSupport.fireEvent(new Event(this, new SpecialTrainTimeIntervalList(
                SpecialTrainTimeIntervalList.Type.MOVED, 0, 0)));
    }

    /**
     * changes time for stop for specified node.
     *
     * @param nodeInterval node interval
     * @param length length of the stop
     */
    public void changeStopTime(TimeInterval nodeInterval, int length) {
        // check time
        if (length < 0) {
            throw new IllegalArgumentException("Stop time cannot be negative.");
        }
        Integer cdst = diagram.getAttribute(TrainDiagram.ATTR_CHANGE_DIRECTION_STOP, Integer.class);
        if (cdst != null && this.checkTimeIntervalChangeDirectionStop(nodeInterval, length)) {
            length = cdst;
        }
        int oldLength = nodeInterval.getLength();
        if (length == oldLength) {
            return;
        }
        int index = intervalList.indexOf(nodeInterval);
        if (index == -1 || index == 0 || index == (intervalList.size() - 1) || !nodeInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Cannot change interval.");
        }

        int changedIndex = index;

        // change stop time
        nodeInterval.setLength(length);

        // compute running time of line before if stop changed
        if (length == 0 || oldLength == 0) {
            changedIndex = index - 1;
        }

        // recalculate intervals
        this.recalculateImpl(changedIndex);

        this.listenerSupport.fireEvent(new Event(this,
                new SpecialTrainTimeIntervalList(SpecialTrainTimeIntervalList.Type.STOP_TIME, changedIndex, index)));
    }

    /**
     * changes velocity of the train on the specified line.
     *
     * @param lineInterval line interval
     * @param speed velocity to be set
     * @param addedTime added time
     */
    public void changeSpeedAndAddedTime(TimeInterval lineInterval, Integer speed, int addedTime) {
        int index = intervalList.indexOf(lineInterval);
        if (index == -1 || !lineInterval.isLineOwner()) {
            throw new IllegalArgumentException("Cannot change interval.");
        }

        lineInterval.setSpeedLimit(speed);
        lineInterval.setAddedTime(addedTime);

        int changedIndex = index;

        // line interval before (if there is not stop ...)
        if (index - 2 >= 0 && intervalList.get(index - 1).getLength() == 0) {
            changedIndex = index - 2;
        }

        // recalculate intervals
        this.recalculateImpl(changedIndex);

        this.listenerSupport.fireEvent(new Event(this,
                new SpecialTrainTimeIntervalList(SpecialTrainTimeIntervalList.Type.SPEED, changedIndex, index)));
    }

    /**
     * changes node track.
     *
     * @param nodeInterval node interval
     * @param nodeTrack node track to be changed
     */
    public void changeNodeTrack(TimeInterval nodeInterval, NodeTrack nodeTrack) {
        if (!nodeInterval.isNodeOwner()) {
            throw new IllegalArgumentException("No node interval.");
        }
        if (nodeInterval.getTrack() == nodeTrack) {
            return;
        }
        new TrainRouteSelection().changeTrack(this, nodeInterval, nodeTrack);
        nodeInterval.setTrack(nodeTrack);

        // update - from/to straight could change
        this.recalculateImpl(0);

        this.listenerSupport
                .fireEvent(new Event(this, new SpecialTrainTimeIntervalList(SpecialTrainTimeIntervalList.Type.TRACK, 0,
                        intervalList.indexOf(nodeInterval))));
    }

    /**
     * changes line track.
     *
     * @param lineInterval line interval
     * @param lineTrack line track to be changed
     */
    public void changeLineTrack(TimeInterval lineInterval, LineTrack lineTrack) {
        if (!lineInterval.isLineOwner()) {
            throw new IllegalArgumentException("No line interval.");
        }
        if (lineInterval.getTrack() == lineTrack) {
            return;
        }
        new TrainRouteSelection().changeTrack(this, lineInterval, lineTrack);

        // update - from/to straight could change
        this.recalculateImpl(0);

        this.listenerSupport
                .fireEvent(new Event(this, new SpecialTrainTimeIntervalList(SpecialTrainTimeIntervalList.Type.TRACK, 0,
                        intervalList.indexOf(lineInterval))));
    }

    /**
     * recalculates all line intervals.
     */
    public void recalculate() {
        this.checkChangeDirectionStops();
        Integer changeStart = this.recalculateImpl(0);
        if (changeStart != null) {
            this.listenerSupport.fireEvent(new Event(this, new SpecialTrainTimeIntervalList(
                    SpecialTrainTimeIntervalList.Type.RECALCULATE, changeStart, changeStart)));
        }
    }

    /**
     * assigns empty tracks (current ones are preselected).
     */
    public void assignEmptyTracks() {
        final TrainRouteTracksComputation computation = new TrainRouteTracksComputation();
        final Map<TimeInterval, Set<Track>> availableTracks = computation.getAvailableTracksForTrain(this);
        final TrackSelectionComputation select = new TrackSelectionComputation();
        if (availableTracks.isEmpty()) {
            throw new GrafikonException("No route for train");
        }
        for (TimeInterval interval : this.intervalList) {
            final Set<? extends Track> intervalTracks = availableTracks.get(interval);
            final Track previousTrack = interval.isFirst() ? null : interval.getPreviousTrainInterval().getTrack();
            interval.setTrack(select.selectTrack(interval, interval.getTrack(), previousTrack, intervalTracks));
        }
        this.recalculate();
    }

    /**
     * implementation of recalculating train intervals.
     */
    private Integer recalculateImpl(int from) {
        if (intervalList.size() <= from) {
            return null;
        }
        Integer firstChanged = null;
        int nextStart = intervalList.get(from).getStart();
        for (int i = from; i < intervalList.size(); i++) {
            TimeInterval interval = intervalList.get(i);
            interval.move(nextStart);
            if (intervalList.updateInterval(interval) && firstChanged == null) {
                firstChanged = i;
            }

            nextStart = interval.getEnd();
        }
        this.updateTechnologicalTimes();
        return firstChanged;
    }

    private void checkChangeDirectionStops() {
        Integer defaultStopTime = diagram.getAttribute(TrainDiagram.ATTR_CHANGE_DIRECTION_STOP, Integer.class);
        if (defaultStopTime == null) {
            return;
        }
        for (TimeInterval interval : intervalList) {
            if (checkTimeIntervalChangeDirectionStop(interval, interval.getLength())) {
                interval.setLength(defaultStopTime);
            }
        }
    }

    private boolean checkTimeIntervalChangeDirectionStop(TimeInterval interval, int length) {
        return interval.isNodeOwner() && length == 0 && interval.isDirectionChange();
    }

    /**
     * adds interval to the train.
     *
     * @param interval interval
     */
    public void addInterval(TimeInterval interval) {
        if (isAttached()) {
            throw new IllegalStateException("Cannot add interval to already attached train.");
        }
        intervalList.addIntervalLastForTrain(interval);
        this.listenerSupport
                .fireEvent(new Event(this, new SpecialTrainTimeIntervalList(SpecialTrainTimeIntervalList.Type.ADDED, 0,
                        intervalList.size() - 1)));
    }

    /**
     * @see TimeIntervalList#getInterval(TimeInterval, int)
     */
    public TimeInterval getInterval(TimeInterval interval, int relativeIndex) {
        return intervalList.getInterval(interval, relativeIndex);
    }

    public TimeInterval getIntervalBefore(TimeInterval interval) {
        return this.getInterval(interval, -1);
    }

    public TimeInterval getIntervalAfter(TimeInterval interval) {
        return this.getInterval(interval, 1);
    }

    /**
     * returns list of intervals.
     *
     * @param from from interval
     * @param to to interval
     * @return list of intervals
     */
    public List<TimeInterval> getIntervals(TimeInterval from, TimeInterval to) {
        TimeInterval lFrom = from == null ? this.getFirstInterval() : from;
        TimeInterval lTo = to == null ? this.getLastInterval() : to;
        int fromIndex = intervalList.indexOf(lFrom);
        int toIndex = intervalList.indexOf(lTo);
        if (fromIndex == -1 || toIndex == -1) {
            throw new IllegalArgumentException("Interval not part of the train.");
        }
        return Collections.unmodifiableList(intervalList.subList(fromIndex, toIndex + 1));
    }

    public int getIndexOfInterval(TimeInterval interval) {
        return intervalList.indexOf(interval);
    }

    public int getAccPenalty(int speed) {
        return PenaltyTable.getAccPenalty(this, speed);
    }

    public int getDecPenalty(int speed) {
        return PenaltyTable.getDecPenalty(this, speed);
    }

    /**
     * attaches the train to the net. It adds time intervals to nodes and lines.
     */
    protected void attach() {
        if (attached) {
            throw new IllegalStateException("Train already attached.");
        }
        for (TimeInterval interval : intervalList) {
            interval.addToOwner();
        }
        if (timeBefore != null) {
            timeBefore.addToOwner();
        }
        if (timeAfter != null) {
            timeAfter.addToOwner();
        }
        // trim size of time interval list to save space (once attached, it cannot be changed)
        intervalList.trimToSize();
        attached = true;
    }

    /**
     * detaches the train from the net. It removes time intervals from the nodes
     * and lines.
     */
    protected void detach() {
        if (!attached) {
            throw new IllegalStateException("Train already detached.");
        }
        for (TimeInterval interval : intervalList) {
            interval.removeFromOwner();
        }
        if (timeBefore != null) {
            timeBefore.removeFromOwner();
        }
        if (timeAfter != null) {
            timeAfter.removeFromOwner();
        }
        attached = false;
    }

    /**
     * fires given train event for this train.
     *
     * @param event train event
     */
    void fireEvent(Event event) {
        listenerSupport.fireEvent(event);
    }

    /**
     * returns if the train is covered by this type of the trains cycle.
     *
     * @param type trains cycle type
     * @return covered
     */
    public boolean isCovered(TrainsCycleType type) {
        return cachedCycles.isCovered(intervalList, type);
    }

    /**
     * returns if the train interval is covered by this type of the trains cycle.
     *
     * @param type trains cycle type
     * @param interval time interval
     * @return covered
     */
    public boolean isCovered(TrainsCycleType type, TimeInterval interval) {
        return !cachedCycles.get(interval, type).isEmpty();
    }

    /**
     * returns if the train interval is covered by specified type
     *
     * @param cycle trains cycle
     * @param interval interval
     * @return covered
     */
    public boolean isCovered(TrainsCycle cycle, TimeInterval interval) {
        Collection<TrainsCycleItem> list = cachedCycles.get(interval, cycle.getType());
        for (TrainsCycleItem item : list) {
            if (item.getCycle() == cycle) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns pair of nodes that indicates first uncovered interval.
     *
     * @param type trains cycle type
     * @return interval
     */
    public Tuple<TimeInterval> getFirstUncoveredPart(TrainsCycleType type) {
        List<Tuple<TimeInterval>> tuples = cachedCycles.getUncovered(intervalList, type);
        return tuples.isEmpty() ? null : tuples.get(0);
    }

    /**
     * returns all uncovered parts.
     *
     * @param type trains cycle type
     * @return list of intervals
     */
    public List<Tuple<TimeInterval>> getAllUncoveredParts(TrainsCycleType type) {
        return cachedCycles.getUncovered(intervalList, type);
    }

    /**
     * returns coverage.
     *
     * @param type trains cycle type
     * @return list of intervals
     */
    public List<Pair<TimeInterval, Boolean>> getRouteCoverage(TrainsCycleType type) {
        return cachedCycles.getCoverage(intervalList, type);
    }

    /**
     * tests if the item can be safely added.
     * @param newItem trains cycle item
     * @param ignoredItem ignored trains cycle item
     * @param overlapping if the new item can be overlapped with the existing ones
     * @return if can be safely added
     */
    public boolean testAddCycle(TrainsCycleItem newItem, TrainsCycleItem ignoredItem, boolean overlapping) {
        return cachedCycles.testAddCycle(intervalList, newItem, ignoredItem, overlapping);
    }

    /**
     * @return first time interval of the train
     */
    public TimeInterval getFirstInterval() {
        return intervalList.get(0);
    }

    /**
     * return last time interval of the train.
     * @return last time interval
     */
    public TimeInterval getLastInterval() {
        return intervalList.get(intervalList.size() - 1);
    }

    /**
     * return interval specified by id.
     * @param id id of the interval
     * @return interval
     */
    public TimeInterval getIntervalById(String id) {
        for (TimeInterval interval : intervalList) {
            if (interval.getId().equals(id)) {
                return interval;
            }
        }
        return null;
    }

    /**
     * returns if the train is attached to net.
     * @return attached
     */
    public boolean isAttached() {
        return attached;
    }

    public boolean isElectric() {
        return this.getAttributes().getBool(ATTR_ELECTRIC);
    }

    public boolean isDiesel() {
        return this.getAttributes().getBool(ATTR_DIESEL);
    }

    public boolean isOptional() {
        return this.getAttributes().getBool(ATTR_OPTIONAL);
    }

    public boolean isEmpty() {
        return this.getAttributes().getBool(ATTR_EMPTY);
    }

    public boolean isManagedFreight() {
        return this.getAttributeAsBool(Train.ATTR_MANAGED_FREIGHT);
    }

    public Train getPreviousJoinedTrain() {
        return previousJoinedTrain;
    }

    private void setPreviousJoinedTrain(Train oldPreviousTrain, Train previousTrain) {
        if (previousTrain != oldPreviousTrain) {
            previousJoinedTrain = previousTrain;
            this.fireEvent(new Event(this,
                    new AttributeChange(Train.ATTR_PREVIOUS_JOINED_TRAIN, oldPreviousTrain, previousJoinedTrain)));
        }
    }

    public Train getNextJoinedTrain() {
        return this.getAttributes().get(ATTR_NEXT_JOINED_TRAIN, Train.class);
    }

    public void setNextJoinedTrain(Train nextTrain) {
        this.getAttributes().setRemove(ATTR_NEXT_JOINED_TRAIN, nextTrain);
    }

    @Override
    public Iterator<TimeInterval> iterator() {
        return intervalList.iterator();
    }

    public Map<String, Object> getBinding() {
        return nameDelegate.createTemplateBinding();
    }

    public Map<String, Object> getBinding(Locale locale) {
        return nameDelegate.createTemplateBinding(locale);
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
