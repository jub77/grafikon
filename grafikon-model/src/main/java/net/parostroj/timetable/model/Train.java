package net.parostroj.timetable.model;

import java.util.*;
import net.parostroj.timetable.actions.TrainsCycleHelper;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.model.events.TrainEvent.TimeIntervalListType;
import net.parostroj.timetable.model.events.TrainListener;
import net.parostroj.timetable.utils.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Train.
 *
 * @author jub
 */
public class Train implements AttributesHolder, ObjectWithId, Visitable {

    /** No top speed constant. */
    public static final int NO_TOP_SPEED = 0;

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
    private TimeIntervalList timeIntervalList;
    /** Top speed (comment - 0 .. no speed defined (speed should be determined by train type)). */
    private int topSpeed = NO_TOP_SPEED;
    /** Cycles. */
    private Map<TrainsCycleType, List<TrainsCycleItem>> cycles;
    /* Attributes of the train. */
    private Attributes attributes;
    /* cached data */
    private String _cachedName;
    private String _cachedCompleteName;
    private GTListenerSupport<TrainListener, TrainEvent> listenerSupport;
    private boolean attached;

    /* Technological times. */
    private TimeInterval timeBefore;
    private TimeInterval timeAfter;

    /**
     * Constructor.
     *
     * @param id id
     * @param diagram train diagram
     */
    Train(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        timeIntervalList = new TimeIntervalList();
        attributes = new Attributes();
        cycles = new EnumMap<TrainsCycleType, List<TrainsCycleItem>>(TrainsCycleType.class);
        listenerSupport = new GTListenerSupport<TrainListener, TrainEvent>(new GTEventSender<TrainListener, TrainEvent>() {

            @Override
            public void fireEvent(TrainListener listener, TrainEvent event) {
                listener.trainChanged(event);
            }
        });
        attached = false;
        timeBefore = null;
        timeAfter = null;
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
        this.clearCachedData();
        String oldNumber = this.number;
        this.number = number;
        this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange("number", oldNumber, number)));
    }

    /**
     * @return name of the train depending on the pattern
     */
    public String getName() {
        if (_cachedName == null) {
            _cachedName = type.formatTrainName(this);
        }
        return _cachedName;
    }

    /**
     * @return complete name of the train depending on the pattern
     */
    public String getCompleteName() {
        if (_cachedCompleteName == null) {
            _cachedCompleteName = type.formatTrainCompleteName(this);
        }
        return _cachedCompleteName;
    }

    /**
     * clears cached train names.
     */
    public void clearCachedData() {
        _cachedCompleteName = null;
        _cachedName = null;
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
        this.clearCachedData();
        String oldDesc = this.description;
        this.description = description;
        this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange("description", oldDesc, description)));
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
        int oldSpeed = this.topSpeed;
        this.topSpeed = topSpeed;
        this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange("topSpeed", oldSpeed, topSpeed)));
    }

    @Override
    public String toString() {
        return number + "(" + ((type != null) ? type.getDesc() : "<none>") + "," + topSpeed + ")";
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
        this.clearCachedData();
        TrainType oldType = this.type;
        this.type = type;
        this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange("type", oldType, type)));
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
    public Map<TrainsCycleType, List<TrainsCycleItem>> getCyclesMap() {
        EnumMap<TrainsCycleType, List<TrainsCycleItem>> modMap = new EnumMap<TrainsCycleType, List<TrainsCycleItem>>(TrainsCycleType.class);
        for (Map.Entry<TrainsCycleType, List<TrainsCycleItem>> entry : cycles.entrySet()) {
            modMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(cycles);
    }

    /**
     * @param type type of the cycles
     * @return list with trains cycle items of specified type (list cannot be modified)
     */
    public List<TrainsCycleItem> getCycles(TrainsCycleType type) {
        return Collections.unmodifiableList(this.getCyclesIntern(type));
    }

    /**
     * @param type type of the cycles
     * @return list with trains cycle items - for internal use only, the list can be modified
     */
    private List<TrainsCycleItem> getCyclesIntern(TrainsCycleType type) {
        if (!cycles.containsKey(type)) {
            cycles.put(type, new LinkedList<TrainsCycleItem>());
        }
        return cycles.get(type);
    }

    /**
     * @param item train cycle item to be added
     */
    protected void addCycleItem(TrainsCycleItem item) {
        TrainsCycleType cycleType = item.getCycle().getType();
        TrainsCycleHelper.getHelper().addCycleItem(this.getTimeIntervalList(), this.getCyclesIntern(cycleType), item, true);
        this.listenerSupport.fireEvent(new TrainEvent(this, GTEventType.CYCLE_ITEM_ADDED, item));
    }

    /**
     * @param item train cycle item to be removed
     */
    protected void removeCycleItem(TrainsCycleItem item) {
        TrainsCycleType cycleType = item.getCycle().getType();
        this.getCyclesIntern(cycleType).remove(item);
        this.listenerSupport.fireEvent(new TrainEvent(this, GTEventType.CYCLE_ITEM_REMOVED, item));
    }

    /**
     * @param type trains cycle type
     * @param interval time interval
     * @return train cycle item that covers given interval (if there are more than one interval,
     * it returns the first one)
     */
    public TrainsCycleItem getCycleItemForInterval(TrainsCycleType type, TimeInterval interval) {
        List<TrainsCycleItem> items = this.getCyclesIntern(type);
        for (TrainsCycleItem item : items) {
            if (item.containsInterval(interval))
                return item;
        }
        return null;
    }

    /**
     * @return attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * @param attributes attributes to be set
     */
    public void setAttributes(Attributes attributes) {
        this.clearCachedData();
        this.attributes = attributes;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        Object o = attributes.remove(key);
        if (o != null)
            this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange(key, o, null)));
        return o;
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.clearCachedData();
        Object oldValue = attributes.get(key);
        attributes.put(key, value);
        this.listenerSupport.fireEvent(new TrainEvent(this, new AttributeChange(key, oldValue, value)));
    }

    // methods for testing/attaching/detaching trains in/to/from net
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
        return timeIntervalList.get(0).getOwner().asNode();
    }

    /**
     * returns last node of the train.
     *
     * @return last node
     */
    public Node getEndNode() {
        return timeIntervalList.get(timeIntervalList.size() - 1).getOwner().asNode();
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
            if (isAttached())
                timeBefore.removeFromOwner();
            timeBefore = null;
        } else if (length != 0 && timeBefore == null) {
            TimeInterval firstInterval = this.getFirstInterval();
            timeBefore = new TimeInterval(IdGenerator.getInstance().getId(), this, firstInterval.getOwner(),
                    firstInterval.getStart() - length, firstInterval.getStart() - 1,
                    firstInterval.getTrack());
            if (isAttached())
                timeBefore.addToOwnerWithoutCheck();
        } else if (length != 0 && timeBefore != null) {
            TimeInterval firstInterval = this.getFirstInterval();
            // recalculate time
            timeBefore.setStart(firstInterval.getStart() - length);
            timeBefore.setLength(length - 1);
            timeBefore.setTrack(firstInterval.getTrack());
            if (isAttached())
                timeBefore.updateInOwner();
        } else {
            fireEvent = false;
        }
        if (fireEvent)
            listenerSupport.fireEvent(new TrainEvent(this, GTEventType.TECHNOLOGICAL));
    }

    /**
     * @param length technological time after train end
     */
    public void setTimeAfter(int length) {
        boolean fireEvent = true;
        if (length == 0 && timeAfter != null) {
            if (isAttached())
                timeAfter.removeFromOwner();
            timeAfter = null;
        } else if (length != 0 && timeAfter == null) {
            TimeInterval lastInterval = this.getLastInterval();
            timeAfter = new TimeInterval(IdGenerator.getInstance().getId(), this, lastInterval.getOwner(),
                    lastInterval.getEnd() + 1, lastInterval.getEnd() + length,
                    lastInterval.getTrack());
            if (isAttached())
                timeAfter.addToOwnerWithoutCheck();
        } else if (length != 0 && timeAfter != null) {
            TimeInterval lastInterval = this.getLastInterval();
            // recalculate time
            timeAfter.setStart(lastInterval.getEnd() + 1);
            timeAfter.setLength(length - 1);
            timeAfter.setTrack(lastInterval.getTrack());
            if (isAttached())
                timeAfter.updateInOwner();
        } else {
            fireEvent = false;
        }
        if (fireEvent)
            listenerSupport.fireEvent(new TrainEvent(this, GTEventType.TECHNOLOGICAL));
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
     * shifts train with specified amount of time. The value can be
     * positive for shifting forwards or negative for shifting backwards.
     *
     * @param timeShift time
     */
    public void shift(int timeShift) {
        timeIntervalList.shift(timeShift);
        this.updateTechnologicalTimes();
        this.listenerSupport.fireEvent(new TrainEvent(this,
                TimeIntervalListType.MOVED,
                0, 0));
    }

    /**
     * moves train to the new starting time.
     *
     * @param time starting time
     */
    public void move(int time) {
        timeIntervalList.move(time);
        this.updateTechnologicalTimes();
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
        if (length == nodeInterval.getLength())
            return;
        int index = timeIntervalList.indexOf(nodeInterval);
        if (index == -1 || index == 0 || index == (timeIntervalList.size() - 1)
                || !nodeInterval.isNodeOwner())
            throw new IllegalArgumentException("Cannot change interval.");

        int changeIndex = index;
        int oldLength = nodeInterval.getLength();

        // change stop time
        nodeInterval.setLength(length);

        TimeInterval lineInterval = null;
        // compute running time of line before
        lineInterval = timeIntervalList.get(index - 1);
        if ((length == 0 && oldLength != 0) || (length != 0 && oldLength ==0)) {
            timeIntervalList.updateLineInterval(lineInterval, index - 1);
            // move node interval
            nodeInterval.move(lineInterval.getEnd());
            changeIndex = index - 1;
        }
        // update node interval
        timeIntervalList.updateNodeInterval(nodeInterval, index);
        // compute running time of line after
        lineInterval = timeIntervalList.get(index + 1);
        lineInterval.move(nodeInterval.getEnd());
        timeIntervalList.updateLineInterval(lineInterval, index + 1);
        // move rest
        timeIntervalList.moveFrom(index + 2, lineInterval.getEnd());
        this.updateTechnologicalTimeAfter();
        this.listenerSupport.fireEvent(new TrainEvent(this,
                TimeIntervalListType.STOP_TIME,
                index, changeIndex));
    }

    /**
     * changes velocity of the train on the specified line.
     *
     * @param lineInterval line interval
     * @param speed velocity to be set
     * @param modelInfo model info
     */
    public void changeSpeed(TimeInterval lineInterval, int speed) {
        int index = timeIntervalList.indexOf(lineInterval);
        if (index == -1 || !lineInterval.isLineOwner())
            throw new IllegalArgumentException("Cannot change interval.");

        int computedSpeed = lineInterval.getOwnerAsLine().computeSpeed(this, speed);
        lineInterval.setSpeed(computedSpeed);

        int changedIndex = index;

        // line interval before (if there is not stop ...)
        if (index - 2 >= 0 && timeIntervalList.get(index - 1).getLength() == 0) {
            TimeInterval lti = timeIntervalList.get(index - 2);
            timeIntervalList.updateLineInterval(lti, index - 2);
            TimeInterval nti = timeIntervalList.get(index - 1);
            nti.move(lti.getEnd());
            timeIntervalList.updateNodeInterval(nti, index - 1);
            lineInterval.move(nti.getEnd());
            changedIndex = index - 2;
        }

        // change line interval
        timeIntervalList.updateLineInterval(lineInterval, index);

        // change intervals after (if there is no stop ...)
        if (index + 2 < timeIntervalList.size() && timeIntervalList.get(index + 1).getLength() == 0) {
            TimeInterval nti = timeIntervalList.get(index + 1);
            nti.move(lineInterval.getEnd());
            timeIntervalList.updateNodeInterval(nti, index + 1);
            TimeInterval lti = timeIntervalList.get(index + 2);
            lti.move(nti.getEnd());
            timeIntervalList.updateLineInterval(lti, index + 2);
            // move rest
            timeIntervalList.moveFrom(index + 3, lti.getEnd());
        } else {
            // move rest
            timeIntervalList.moveFrom(index + 1, lineInterval.getEnd());
        }

        this.updateTechnologicalTimeAfter();
        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.SPEED, index, changedIndex));
    }

    /**
     * changes node track.
     *
     * @param nodeInterval node interval
     * @param nodeTrack node track to be changed
     */
    public void changeNodeTrack(TimeInterval nodeInterval, NodeTrack nodeTrack) {
        if (!nodeInterval.isNodeOwner())
            throw new IllegalArgumentException("No node interval.");
        nodeInterval.setTrack(nodeTrack);
        if (isAttached())
            nodeInterval.updateInOwner();
        if (this.getTimeBefore() != 0 && nodeInterval.isFirst())
            this.updateTechnologicalTimeBefore();
        if (this.getTimeAfter() != 0 && nodeInterval.isLast())
            this.updateTechnologicalTimeAfter();
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
        if (isAttached())
            lineInterval.updateInOwner();
        // we do not need to update technological times
        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.TRACK, timeIntervalList.indexOf(lineInterval)));
    }

    /**
     * Recalculates all line intervals.
     *
     * @param info model info
     */
    public void recalculate() {
        int nextStart = this.getStartTime();
        int i = 0;
        for (TimeInterval interval : timeIntervalList) {
            interval.move(nextStart);
            if (interval.isLineOwner()) {
                Line line = (Line) interval.getOwner();
                // compute speed
                int speed = line.computeSpeed(this, interval.getSpeed());
                interval.setSpeed(speed);

                timeIntervalList.updateLineInterval(interval, i);
            } else {
                timeIntervalList.updateNodeInterval(interval, i);
            }

            nextStart = interval.getEnd();
            i++;
        }
        this.updateTechnologicalTimes();
        this.listenerSupport.fireEvent(new TrainEvent(this, TimeIntervalListType.RECALCULATE, 0, 0));
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
     * @param interval interval
     * @return interval directly before given interval
     */
    public TimeInterval getIntervalBefore(TimeInterval interval) {
        return timeIntervalList.getIntervalBefore(interval);
    }

    /**
     * @param interval interval
     * @return interval directly after given interval
     */
    public TimeInterval getIntervalAfter(TimeInterval interval) {
        return timeIntervalList.getIntervalAfter(interval);
    }

    /**
     * returns list of interval.
     *
     * @param from from interval
     * @param to to interval
     * @return list of intervals
     */
    public List<TimeInterval> getIntervals(TimeInterval from, TimeInterval to) {
        List<TimeInterval> intervals = new LinkedList<TimeInterval>();
        boolean in = false;
        for (TimeInterval currentInterval : this.getTimeIntervalList()) {
            if (from == currentInterval)
                in = true;
            if (in)
                intervals.add(currentInterval);
            if (to == currentInterval)
                in = false;
        }
        return intervals;
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
            if (interval.getOwner() instanceof Line) {
                if (!value.equals(((Line) interval.getOwner()).getAttribute(key))) {
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
            if (interval.getOwner() instanceof Node) {
                if (!value.equals(((Node) interval.getOwner()).getAttribute(key))) {
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
            if (interval.getOwner() instanceof Line) {
                if (value.equals(((Line) interval.getOwner()).getAttribute(key))) {
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
            if (interval.getOwner() instanceof Node) {
                if (value.equals(((Node) interval.getOwner()).getAttribute(key))) {
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
        return TrainsCycleHelper.getHelper().isTimeIntervalListCovered(this.getTimeIntervalList(), this.getCyclesIntern(type));
    }

    /**
     * returns if the train interval is covered by this type of the trains cycle.
     *
     * @param type trains cycle type
     * @param interval time interval
     * @return covered
     */
    public boolean isCovered(TrainsCycleType type, TimeInterval interval) {
        return TrainsCycleHelper.getHelper().isTimeIntervalCovered(this.getTimeIntervalList(), this.getCycles(type), interval);
    }

    /**
     * returns if the train interval is covered by specified type
     *
     * @param cycle trains cycle
     * @param interval interval
     * @return covered
     */
    public boolean isCovered(TrainsCycle cycle, TimeInterval interval) {
        return TrainsCycleHelper.getHelper().isTimeIntervalCovered(this.getTimeIntervalList(), cycle.getItems(), interval);
    }

    /**
     * returns pair of nodes that indicates first uncovered interval.
     *
     * @param type trains cycle type
     * @return interval
     */
    public Tuple<TimeInterval> getFirstUncoveredPart(TrainsCycleType type) {
        List<Tuple<TimeInterval>> tuples = TrainsCycleHelper.getHelper().getAllUncoveredParts(this.getTimeIntervalList(), this.getCyclesIntern(type));
        return tuples.isEmpty() ? null : tuples.get(0);
    }

    /**
     * returns all uncovered parts.
     *
     * @param type trains cycle type
     * @return list of intervals
     */
    public List<Tuple<TimeInterval>> getAllUncoveredParts(TrainsCycleType type) {
        return TrainsCycleHelper.getHelper().getAllUncoveredParts(this.getTimeIntervalList(), this.getCyclesIntern(type));
    }

    /**
     * returns all uncovered parts (as list of nodes).
     *
     * @param type trains cycle type
     * @return list of intervals (as list of nodes)
     */
    public List<List<TimeInterval>> getAllUncoveredLists(TrainsCycleType type) {
        return TrainsCycleHelper.getHelper().getAllUncoveredLists(this.getTimeIntervalList(), this.getCyclesIntern(type));
    }

    /**
     * returns coverage.
     *
     * @param type trains cycle type
     * @return list of intervals
     */
    public List<Pair<TimeInterval, Boolean>> getRouteCoverage(TrainsCycleType type) {
        return TrainsCycleHelper.getHelper().getTimeIntervalListCoverage(this.getTimeIntervalList(), this.getCyclesIntern(type));
    }

    /**
     * returns list of nodes from from noded to to node.
     *
     * @param from from node
     * @param to to node
     * @return list of nodes in route
     */
    public List<Node> convertToNodeList(Node from, Node to) {
        List<Node> nodes = new LinkedList<Node>();
        boolean collect = false;
        for (TimeInterval interval : getTimeIntervalList()) {
            if (interval.getOwner() instanceof Node) {
                Node node = interval.getOwner().asNode();
                if (from == node)
                    collect = true;
                if (collect)
                    nodes.add(node);
                if (to == node)
                    collect = false;
            }
        }
        return nodes;
    }

    /**
     * tests if the item can be safely added.
     * @param newItem trains cycle item
     * @param ignoredItem ignored trains cycle item
     * @param overlapping if the new item can be overlapped with the existing ones
     * @return if can be safely added
     */
    public boolean testAddCycle(TrainsCycleItem newItem, TrainsCycleItem ignoredItem, boolean overlapping) {
        return TrainsCycleHelper.getHelper().testAddCycle(this.getTimeIntervalList(), getCyclesIntern(newItem.getCycle().getType()), newItem, ignoredItem, overlapping);
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
    public Map<String,Object> createTemplateBinding() {
        Map<String,Object> variables = new HashMap<String, Object>();
        variables.put("train", this);
        variables.put("type", this.getType());
        return variables;
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
