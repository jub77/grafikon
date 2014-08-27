package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.TrainEvent.TimeIntervalListType;
import net.parostroj.timetable.utils.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

import com.google.common.collect.*;

/**
 * Train.
 *
 * @author jub
 */
public class Train implements AttributesHolder, ObjectWithId, Visitable, TrainAttributes {

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
    private final TimeIntervalList timeIntervalList;
    /** Top speed. */
    private int topSpeed = Integer.MAX_VALUE;
    /** Cycles. */
    private final ListMultimap<TrainsCycleType, TrainsCycleItem> cycles;
    /* Attributes of the train. */
    private Attributes attributes;
    /* cached data */
    private final CachedValue<String> _cachedName;
    private final CachedValue<String> _cachedCompleteName;
    private Map<String,Object> _cachedBinding;
    private final GTListenerSupport<TrainListener, TrainEvent> listenerSupport;
    private AttributesListener attributesListener;
    private boolean attached;

    /* Technological times. */
    private TimeInterval timeBefore;
    private TimeInterval timeAfter;

    /* Cached map for train cycles. */
    private final TrainCachedCycles _cachedCycles;

    /**
     * Constructor.
     *
     * @param id id
     * @param diagram train diagram
     */
    Train(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        _cachedCycles = new TrainCachedCycles();
        _cachedName = new CachedValue<String>();
        _cachedCompleteName = new CachedValue<String>();
        timeIntervalList = new TimeIntervalList();
        this.setAttributes(new Attributes());
        cycles = LinkedListMultimap.create();
        listenerSupport = new GTListenerSupport<TrainListener, TrainEvent>(new GTEventSender<TrainListener, TrainEvent>() {
            @Override
            public void fireEvent(TrainListener listener, TrainEvent event) {
                listener.trainChanged(event);
            }
        });
        attached = false;
        timeBefore = null;
        timeAfter = null;
        this.setAttribute(ATTR_DIESEL, false);
        this.setAttribute(ATTR_ELECTRIC, false);
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
    public TrainDiagram getTrainDiagram() {
        return diagram;
    }

    /**
     * adds listener to train.
     * @param listener listener
     */
    public void addListener(TrainListener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener from train.
     * @param listener listener
     */
    public void removeListener(TrainListener listener) {
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
            this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange(ATTR_NUMBER, oldNumber, number)));
            this.refreshCachedNames();
        }
    }

    /**
     * @return name of the train depending on the pattern
     */
    public String getName() {
        if (!_cachedName.isCached()) {
            this.refreshName();
        }
        return _cachedName.getValue();
    }

    private String getNameImpl() {
        return type != null ? type.formatTrainName(this) : number;
    }

    /**
     * @return complete name of the train depending on the pattern
     */
    public String getCompleteName() {
        if (!_cachedCompleteName.isCached()) {
            this.refreshCompleteName();
        }
        return _cachedCompleteName.getValue();
    }

    private String getCompleteNameImpl() {
        return type != null ? type.formatTrainCompleteName(this) : number;
    }

    private void refreshName() {
        String oldName = _cachedName.getValue();
        String newName = this.getNameImpl();
        if (_cachedName.set(newName)) {
            this.fireEvent(new TrainEvent(this, new AttributeChange(ATTR_NAME, oldName, newName)));
        }
    }

    private void refreshCompleteName() {
        String oldName = _cachedCompleteName.getValue();
        String newName = this.getCompleteNameImpl();
        if (_cachedCompleteName.set(newName)) {
            this.fireEvent(new TrainEvent(this, new AttributeChange(ATTR_COMPLETE_NAME, oldName, newName)));
        }
    }

    /**
     * clears cached train names.
     */
    public void refreshCachedNames() {
        refreshName();
        refreshCompleteName();
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
            this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange(ATTR_DESCRIPTION, oldDesc,
                    description)));
            this.refreshCachedNames();
        }
    }

    /**
     * @return top speed
     */
    public int getTopSpeed() {
        return topSpeed;
    }

    /**
     * @param topSpeed top speed to be set
     */
    public void setTopSpeed(int topSpeed) {
        if (this.topSpeed != topSpeed) {
            int oldSpeed = this.topSpeed;
            this.topSpeed = topSpeed;
            this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange(ATTR_TOP_SPEED, oldSpeed, topSpeed)));
            this.recalculate();
        }
    }

    @Override
    public String toString() {
        return getName();
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
            this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange(ATTR_TYPE, oldType, type)));
            this.refreshCachedNames();
            // penalties can be changed
            this.recalculate();
        }
    }

    /**
     * @return the intervals
     */
    public List<TimeInterval> getTimeIntervalList() {
        return Collections.unmodifiableList(timeIntervalList);
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

    public List<TrainsCycleItem> getCycles(String typeName) {
        TrainsCycleType type = diagram.getCycleType(typeName);
        return this.getCycles(type);
    }

    /**
     * @param item train cycle item to be added
     */
    protected void addCycleItem(TrainsCycleItem item) {
        TrainsCycleType cycleType = item.getCycle().getType();
        _cachedCycles.addCycleItem(timeIntervalList, cycles.get(cycleType), item, true);
        _cachedCycles.add(timeIntervalList, item);
        this.listenerSupport.fireEvent(new TrainEvent(this, GTEventType.CYCLE_ITEM_ADDED, item));
        this.checkRecalculateCycle(item.getCycle());
    }

    /**
     * @param item train cycle item to be removed
     */
    protected void removeCycleItem(TrainsCycleItem item) {
        TrainsCycleType cycleType = item.getCycle().getType();
        this.cycles.remove(cycleType, item);
        _cachedCycles.remove(item);
        this.listenerSupport.fireEvent(new TrainEvent(this, GTEventType.CYCLE_ITEM_REMOVED, item));
        this.checkRecalculateCycle(item.getCycle());
    }

    private void checkRecalculateCycle(TrainsCycle cycle) {
        if (cycle.getType().getName().equals(TrainsCycleType.ENGINE_CYCLE) && cycle.getAttributes().containsKey(TrainsCycle.ATTR_ENGINE_CLASS)) {
            this.recalculate();
        }
    }

    /**
     * @param type trains cycle type
     * @param interval time interval
     * @return list of train cycle items that covers given interval (empty list if there are none)
     */
    public Collection<TrainsCycleItem> getCycleItemsForInterval(TrainsCycleType type, TimeInterval interval) {
        return _cachedCycles.get(interval, type);
    }

    /**
     * @return attributes
     */
    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * @param attributes attributes to be set
     */
    @Override
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                listenerSupport.fireEvent(new TrainEvent(Train.this, change));
                refreshCachedNames();
                if (change.checkName(Train.ATTR_WEIGHT_LIMIT)) {
                    Train.this.recalculate();
                }
                if (change.checkName(Train.ATTR_MANAGED_FREIGHT) && !Boolean.TRUE.equals(change.getNewValue())) {
                    for (TimeInterval interval : timeIntervalList) {
                        interval.removeAttribute(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
                    }
                }
            }
        };
        this.attributes.addListener(attributesListener);
        this.refreshCachedNames();
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    /**
     * @return <code>true</code> if there are overlapping intervals for the train
     */
    public boolean isConflicting() {
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isOverlapping()) {
                return true;
            }
        }
        // test technological times
        if ((timeBefore != null && timeBefore.isOverlapping()) ||
                (timeAfter != null && timeAfter.isOverlapping()))
            return true;
        // no conflict found
        return false;
    }

    /**
     * @return set of train with conflicts with this train
     */
    public Set<Train> getConflictingTrains() {
        ReferenceHolder<Set<Train>> conflictsRef = new ReferenceHolder<Set<Train>>();
        for (TimeInterval interval : timeIntervalList) {
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
                    conflictsRef.set(new HashSet<Train>());
                }
                conflictsRef.get().add(i2.getTrain());
            }
        }
    }

    /**
     * @return <code>true</code> if all stops that needs platform have one
     */
    public boolean checkPlatforms() {
        for (TimeInterval interval : timeIntervalList) {
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
        return timeIntervalList.get(0).getOwnerAsNode();
    }

    /**
     * returns last node of the train.
     *
     * @return last node
     */
    public Node getEndNode() {
        return timeIntervalList.get(timeIntervalList.size() - 1).getOwnerAsNode();
    }

    /**
     * returns start time of the train.
     *
     * @return start time
     */
    public int getStartTime() {
        return timeIntervalList.get(0).getStart();
    }

    /**
     * returns end time of the train.
     *
     * @return end time
     */
    public int getEndTime() {
        return timeIntervalList.get(timeIntervalList.size() - 1).getEnd();
    }

    /**
     * @param length technological time before train start
     */
    public void setTimeBefore(int length) {
        boolean fireEvent = true;
        if (length == 0 && timeBefore != null) {
            if (isAttached()) {
                timeBefore.removeFromOwner();
            }
            timeBefore = null;
        } else if (length != 0 && timeBefore == null) {
            TimeInterval firstInterval = this.getFirstInterval();
            timeBefore = new TimeInterval(IdGenerator.getInstance().getId(), this, firstInterval.getOwner(),
                    firstInterval.getStart() - length, firstInterval.getStart() - 1,
                    firstInterval.getTrack());
            if (isAttached()) {
                timeBefore.addToOwnerWithoutCheck();
            }
        } else if (length != 0 && timeBefore != null) {
            TimeInterval firstInterval = this.getFirstInterval();
            // recalculate time
            timeBefore.setStart(firstInterval.getStart() - length);
            timeBefore.setLength(length - 1);
            timeBefore.setTrack(firstInterval.getTrack());
            fireEvent = timeBefore.isChanged();
            if (isAttached()) {
                timeBefore.updateInOwner();
            }
        } else {
            fireEvent = false;
        }
        if (fireEvent) {
            listenerSupport.fireEvent(new TrainEvent(this, GTEventType.TECHNOLOGICAL));
        }
    }

    /**
     * @param length technological time after train end
     */
    public void setTimeAfter(int length) {
        boolean fireEvent = true;
        if (length == 0 && timeAfter != null) {
            if (isAttached()) {
                timeAfter.removeFromOwner();
            }
            timeAfter = null;
        } else if (length != 0 && timeAfter == null) {
            TimeInterval lastInterval = this.getLastInterval();
            timeAfter = new TimeInterval(IdGenerator.getInstance().getId(), this, lastInterval.getOwner(),
                    lastInterval.getEnd() + 1, lastInterval.getEnd() + length,
                    lastInterval.getTrack());
            if (isAttached()) {
                timeAfter.addToOwnerWithoutCheck();
            }
        } else if (length != 0 && timeAfter != null) {
            TimeInterval lastInterval = this.getLastInterval();
            // recalculate time
            timeAfter.setStart(lastInterval.getEnd() + 1);
            timeAfter.setLength(length - 1);
            timeAfter.setTrack(lastInterval.getTrack());
            fireEvent = timeAfter.isChanged();
            if (isAttached()) {
                timeAfter.updateInOwner();
            }
        } else {
            fireEvent = false;
        }
        if (fireEvent) {
            listenerSupport.fireEvent(new TrainEvent(this, GTEventType.TECHNOLOGICAL));
        }
    }

    /**
     * @return technological time before train start
     */
    public int getTimeBefore() {
        return (timeBefore == null) ? 0 : timeBefore.getLength() + 1;
    }

    /**
     * @return technological time after train end
     */
    public int getTimeAfter() {
        return (timeAfter == null) ? 0 : timeAfter.getLength() + 1;
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

    public void updateTechnologicalTimeAfter() {
        this.setTimeAfter(this.getTimeAfter());
    }

    public void updateTechnologicalTimeBefore() {
        this.setTimeBefore(this.getTimeBefore());
    }

    /**
     * moves train to the new starting time.
     *
     * @param time starting time
     */
    public void move(int time) {
    	timeIntervalList.get(0).move(time);
    	this.recalculateImpl(0);
        this.listenerSupport.fireEvent(new TrainEvent(this,
                TimeIntervalListType.MOVED,
                0, 0));
    }

    /**
     * changes time for stop for specified node.
     *
     * @param nodeInterval node interval
     * @param length length of the stop
     * @param info model info
     */
    public void changeStopTime(TimeInterval nodeInterval, int length) {
        // check time
        if (length < 0) {
            throw new IllegalArgumentException("Stop time cannot be negative.");
        }
        int oldLength = nodeInterval.getLength();
        if (length == oldLength)
            return;
        int index = timeIntervalList.indexOf(nodeInterval);
        if (index == -1 || index == 0 || index == (timeIntervalList.size() - 1) || !nodeInterval.isNodeOwner())
            throw new IllegalArgumentException("Cannot change interval.");

        int changedIndex = index;

        // change stop time
        nodeInterval.setLength(length);

        // compute running time of line before if stop changed
        if (length == 0 || oldLength == 0) {
            changedIndex = index - 1;
        }

        // recalculate intervals
        this.recalculateImpl(changedIndex);

        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.STOP_TIME, index, changedIndex));
    }

    /**
     * changes velocity of the train on the specified line.
     *
     * @param lineInterval line interval
     * @param speed velocity to be set
     * @param addedTime added time
     */
    public void changeSpeedAndAddedTime(TimeInterval lineInterval, Integer speed, int addedTime) {
        int index = timeIntervalList.indexOf(lineInterval);
        if (index == -1 || !lineInterval.isLineOwner())
            throw new IllegalArgumentException("Cannot change interval.");

        lineInterval.setSpeedLimit(speed);
        lineInterval.setAddedTime(addedTime);

        int changedIndex = index;

        // line interval before (if there is not stop ...)
        if (index - 2 >= 0 && timeIntervalList.get(index - 1).getLength() == 0) {
            changedIndex = index - 2;
        }

        // recalculate intervals
        this.recalculateImpl(changedIndex);

        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.SPEED, index, changedIndex));
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
        nodeInterval.setTrack(nodeTrack);
        if (isAttached()) {
            nodeInterval.updateInOwner();
        }
        if (this.getTimeBefore() != 0 && nodeInterval.isFirst()) {
            this.updateTechnologicalTimeBefore();
        }
        if (this.getTimeAfter() != 0 && nodeInterval.isLast()) {
            this.updateTechnologicalTimeAfter();
        }
        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.TRACK, timeIntervalList.indexOf(nodeInterval)));
    }

    /**
     * changes line track.
     *
     * @param lineInterval line interval
     * @param lineTrack line track to be changed
     */
    public void changeLineTrack(TimeInterval lineInterval, LineTrack lineTrack) {
        if (!lineInterval.isLineOwner())
            throw new IllegalArgumentException("No line interval.");
        lineInterval.setTrack(lineTrack);
        if (isAttached()) {
            lineInterval.updateInOwner();
        }
        // we do not need to update technological times
        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.TRACK, timeIntervalList.indexOf(lineInterval)));
    }

    /**
     * recalculates all line intervals.
     */
    public void recalculate() {
        Integer changeStart = this.recalculateImpl(0);
        if (changeStart != null) {
            this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.RECALCULATE, changeStart, changeStart));
        }
    }

    /**
     * assigns empty tracks (current ones are preselected).
     */
    public void assignEmptyTracks() {
        TimeInterval last = null;
        for (TimeInterval interval : this.timeIntervalList) {
            if (interval.isLineOwner()) {
                Line line = interval.getOwnerAsLine();
                interval.setTrack(line.selectTrack(interval, (LineTrack) interval.getTrack()));
            } else {
                Node node = interval.getOwnerAsNode();
                NodeTrack preselected = (NodeTrack) interval.getTrack();
                preselected = (preselected == null && last != null) ? last.getToStraightTrack() : preselected;
                interval.setTrack(node.selectTrack(interval, preselected));
            }
            last = interval;
        }
    }

    /**
     * implementation of recalculating train intervals.
     */
    private Integer recalculateImpl(int from) {
        if (timeIntervalList.size() <= from) {
            return null;
        }
        Integer firstChanged = null;
        int nextStart = timeIntervalList.get(from).getStart();
        for (int i = from; i < timeIntervalList.size(); i++) {
            TimeInterval interval = timeIntervalList.get(i);
            interval.move(nextStart);
            if (timeIntervalList.updateInterval(interval, i) && firstChanged == null) {
                firstChanged = i;
            }

            nextStart = interval.getEnd();
        }
        this.updateTechnologicalTimes();
        return firstChanged;
    }

    /**
     * adds interval to the train.
     *
     * @param interval interval
     */
    public void addInterval(TimeInterval interval) {
        if (isAttached())
            throw new IllegalStateException("Cannot add interval to already attached train.");
        timeIntervalList.addIntervalLastForTrain(interval);
        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.ADDED, timeIntervalList.size() - 1));
    }

    /**
     * @see TimeIntervalList#getInterval(TimeInterval, int)
     */
    public TimeInterval getInterval(TimeInterval interval, int relativeIndex) {
        return timeIntervalList.getInterval(interval, relativeIndex);
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
        int fromIndex = timeIntervalList.indexOf(from);
        int toIndex = timeIntervalList.indexOf(to);
        if (fromIndex == -1 || toIndex == -1)
            throw new IllegalArgumentException("Interval not part of the train.");
        return Collections.unmodifiableList(timeIntervalList.subList(fromIndex, toIndex + 1));
    }

    public int getIndexOfInterval(TimeInterval interval) {
        return timeIntervalList.indexOf(interval);
    }

    /**
     * checks if all lines have given attribute.
     *
     * @param key key
     * @param value value
     * @return result of the check
     */
    public boolean allLinesHaveAttribute(String key, Object value) {
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isLineOwner()) {
                if (!value.equals(interval.getOwnerAsLine().getAttribute(key, Object.class))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks if all nodes have given attribute.
     *
     * @param key key
     * @param value value
     * @return result of the check
     */
    public boolean allNodesHaveAttribute(String key, Object value) {
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isNodeOwner()) {
                if (!value.equals(interval.getOwnerAsNode().getAttribute(key, Object.class))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks if at least one line has given attribute.
     *
     * @param key key
     * @param value value
     * @return result of the check
     */
    public boolean oneLineHasAttribute(String key, Object value) {
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isLineOwner()) {
                if (value.equals(interval.getOwnerAsLine().getAttribute(key, Object.class))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if at least one node has given attribute.
     *
     * @param key key
     * @param value value
     * @return result of the check
     */
    public boolean oneNodeHasAttribute(String key, Object value) {
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isNodeOwner()) {
                if (value.equals(interval.getOwnerAsNode().getAttribute(key, Object.class))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * attaches the train to the net. It adds time intervals to nodes and lines.
     */
    protected void attach() {
        if (attached)
            throw new IllegalStateException("Train already attached.");
        for (TimeInterval interval : timeIntervalList) {
            interval.addToOwner();
        }
        if (timeBefore != null)
            timeBefore.addToOwner();
        if (timeAfter != null)
            timeAfter.addToOwner();
        // trim size of time interval list to save space (once attached, it cannot be changed)
        timeIntervalList.trimToSize();
        attached = true;
    }

    /**
     * detaches the train from the net. It removes time intervals from the nodes
     * and lines.
     */
    protected void detach() {
        if (!attached)
            throw new IllegalStateException("Train already detached.");
        for (TimeInterval interval : timeIntervalList) {
            interval.removeFromOwner();
        }
        if (timeBefore != null)
            timeBefore.removeFromOwner();
        if (timeAfter != null)
            timeAfter.removeFromOwner();
        attached = false;
    }

    /**
     * fires given train event for this train.
     *
     * @param event train event
     */
    void fireEvent(TrainEvent event) {
        listenerSupport.fireEvent(event);
    }

    /**
     * returns if the train is covered by this type of the trains cycle.
     *
     * @param type trains cycle type
     * @return covered
     */
    public boolean isCovered(TrainsCycleType type) {
        return _cachedCycles.isCovered(timeIntervalList, type);
    }

    /**
     * returns if the train interval is covered by this type of the trains cycle.
     *
     * @param type trains cycle type
     * @param interval time interval
     * @return covered
     */
    public boolean isCovered(TrainsCycleType type, TimeInterval interval) {
        return !_cachedCycles.get(interval, type).isEmpty();
    }

    /**
     * returns if the train interval is covered by specified type
     *
     * @param cycle trains cycle
     * @param interval interval
     * @return covered
     */
    public boolean isCovered(TrainsCycle cycle, TimeInterval interval) {
        Collection<TrainsCycleItem> list = _cachedCycles.get(interval, cycle.getType());
        for (TrainsCycleItem item : list) {
            if (item.getCycle() == cycle)
                return true;
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
        List<Tuple<TimeInterval>> tuples = _cachedCycles.getUncovered(timeIntervalList, type);
        return tuples.isEmpty() ? null : tuples.get(0);
    }

    /**
     * returns all uncovered parts.
     *
     * @param type trains cycle type
     * @return list of intervals
     */
    public List<Tuple<TimeInterval>> getAllUncoveredParts(TrainsCycleType type) {
        return _cachedCycles.getUncovered(timeIntervalList, type);
    }

    /**
     * returns coverage.
     *
     * @param type trains cycle type
     * @return list of intervals
     */
    public List<Pair<TimeInterval, Boolean>> getRouteCoverage(TrainsCycleType type) {
        return _cachedCycles.getCoverage(timeIntervalList, type);
    }

    /**
     * tests if the item can be safely added.
     * @param newItem trains cycle item
     * @param ignoredItem ignored trains cycle item
     * @param overlapping if the new item can be overlapped with the existing ones
     * @return if can be safely added
     */
    public boolean testAddCycle(TrainsCycleItem newItem, TrainsCycleItem ignoredItem, boolean overlapping) {
        return _cachedCycles.testAddCycle(timeIntervalList, newItem, ignoredItem, overlapping);
    }

    /**
     * @return first time interval of the train
     */
    public TimeInterval getFirstInterval() {
        return timeIntervalList.get(0);
    }

    /**
     * return last time interval of the train.
     * @return last time interval
     */
    public TimeInterval getLastInterval() {
        return timeIntervalList.get(timeIntervalList.size() - 1);
    }

    /**
     * return interval specified by id.
     * @param id id of the interval
     * @return interval
     */
    public TimeInterval getIntervalById(String id) {
        for (TimeInterval interval : timeIntervalList) {
            if (interval.getId().equals(id))
                return interval;
        }
        return null;
    }

    public Route getBestRouteMatch() {
        Pair<Route, Integer> selected = null;
        for (Route route : diagram.getRoutes()) {
            int cnt = 0;
            for (RouteSegment segment : route) {
                boolean containTrain = false;
                for (TimeInterval interval : segment.getTimeIntervals()) {
                    if (interval.getTrain() == this) {
                        containTrain = true;
                        break;
                    }
                }
                if (containTrain) {
                    cnt++;
                }
            }
            if (cnt > 0 && (selected == null || selected.second < cnt)) {
                selected = new Pair<Route, Integer>(route, cnt);
            }
        }
        return selected != null ? selected.first : null;
    }

    /**
     * returns if the train is attached to net.
     * @return attached
     */
    public boolean isAttached() {
        return attached;
    }

    /**
     * @return binding for template
     */
    Map<String,Object> createTemplateBinding() {
        if (_cachedBinding == null) {
            _cachedBinding = new HashMap<String, Object>();
            _cachedBinding.put("train", this);
            _cachedBinding.put("stations", new Stations());
        }
        _cachedBinding.put("type", this.getType());
        return _cachedBinding;
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

    /**
     * Wrapper for accessing stations for text templates.
     */
    public class Stations {

        public Node getAt(int index) {
            return timeIntervalList.get(index * 2).getOwnerAsNode();
        }

        public Node get(int index) {
            return getAt(index);
        }

        public Node getFirst() {
            return getFirstInterval().getOwnerAsNode();
        }

        public Node getLast() {
            return getLastInterval().getOwnerAsNode();
        }
    }
}
