package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Track between two route points.
 *
 * @author jub
 */
public class Line implements RouteSegment, AttributesHolder, ObjectWithId, Visitable, LineAttributes {

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** ID. */
    private final String id;
    /** Length in mm. */
    private int length;
    /** List of node tracks. */
    private final List<LineTrack> tracks;
    /** Top speed for the track. */
    private int topSpeed = UNLIMITED_SPEED;
    /** Unlimited spedd. */
    public static final int UNLIMITED_SPEED = -1;
    /** No speed constant. */
    public static final int NO_SPEED = UNLIMITED_SPEED;
    /** Attributes. */
    private Attributes attributes;
    /** Starting point. */
    private final Node from;
    /** Ending point. */
    private final Node to;
    private final GTListenerSupport<LineListener, LineEvent> listenerSupport;
    private AttributesListener attributesListener;

    /**
     * creates track with specified length.
     *
     * @param id id
     * @param diagram train diagram
     * @param length length of the track in milimeters
     * @param from starting point
     * @param to end point
     * @param topSpeed top speed
     */
    Line(String id, TrainDiagram diagram, int length, Node from, Node to, int topSpeed) {
        tracks = new ArrayList<LineTrack>();
        this.setAttributes(new Attributes());
        this.length = length;
        this.from = from;
        this.to = to;
        this.id = id;
        this.diagram = diagram;
        this.topSpeed = topSpeed;
        this.listenerSupport = new GTListenerSupport<LineListener, LineEvent>(new GTEventSender<LineListener, LineEvent>() {

            @Override
            public void fireEvent(LineListener listener, LineEvent event) {
                listener.lineChanged(event);
            }
        });
    }

    @Override
	public Attributes getAttributes() {
        return attributes;
    }

    @Override
	public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                listenerSupport.fireEvent(new LineEvent(Line.this, change));
            }
        };
        this.attributes.addListener(attributesListener);
    }

    /**
     * @return id of the line
     */
    @Override
    public String getId() {
        return id;
    }

    public TrainDiagram getTrainDiagram() {
        return diagram;
    }

    /**
     * @return track length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length length to be set
     */
    public void setLength(int length) {
        int oldLength = this.length;
        this.length = length;
        this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange("length", oldLength, length)));
    }

    public LineTrack selectTrack(TimeInterval interval, LineTrack preselectedTrack) {
        LineTrack selectedTrack = preselectedTrack;
        if (selectedTrack == null || selectedTrack.testTimeInterval(interval).getStatus() != TimeIntervalResult.Status.OK) {
            // check which track is free for adding
            for (LineTrack lineTrack : this.getIterableByDirection(interval.getDirection())) {
                TimeIntervalResult result = lineTrack.testTimeInterval(interval);
                if (result.getStatus() == TimeIntervalResult.Status.OK) {
                    selectedTrack = lineTrack;
                    break;
                }
            }
        }
        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(interval.getDirection() == TimeIntervalDirection.FORWARD ? 0 : tracks.size() - 1);
        }
        return selectedTrack;
    }

    /**
     * @return tracks
     */
    @Override
    public List<LineTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public void addTrack(LineTrack track) {
        track.line = this;
        tracks.add(track);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TRACK_ADDED, track));
    }

    public void addTrack(LineTrack track, int position) {
        track.line = this;
        tracks.add(position, track);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TRACK_ADDED, track));
    }

    public void removeTrack(LineTrack track) {
        track.line = null;
        tracks.remove(track);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TRACK_REMOVED, track));
    }

    public void moveTrack(LineTrack track, int position) {
        int oldIndex = tracks.indexOf(track);
        this.moveTrack(oldIndex, position);
    }

    public void moveTrack(int fromIndex, int toIndex) {
        LineTrack track = tracks.remove(fromIndex);
        tracks.add(toIndex, track);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TRACK_MOVED, track, fromIndex, toIndex));
    }

    public void removeAllTracks() {
        for (LineTrack track : tracks) {
            track.line = null;
        }
        tracks.clear();
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TRACK_REMOVED));
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
        if (topSpeed == 0 || topSpeed < -1) {
            throw new IllegalArgumentException("Top speed should be positive number.");
        }
        int oldTopSpeed = this.topSpeed;
        this.topSpeed = topSpeed;
        this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange("topSpeed", oldTopSpeed, topSpeed)));
    }

    public int computeSpeed(Train train, TimeInterval interval, int prefferedSpeed) {
        int speed;
        if (prefferedSpeed < 1)
            throw new IllegalArgumentException("Speed has to be greater than 0.");
        if (train.getTopSpeed() != NO_SPEED) {
            speed = Math.min(prefferedSpeed, train.getTopSpeed());
        } else {
            speed = prefferedSpeed;
        }

        // apply track speed limit
        if (this.topSpeed != UNLIMITED_SPEED) {
            speed = Math.min(speed, this.topSpeed);
        }

        // adjust (engine class influence)
        if (interval != null) {
            List<EngineClass> engineClasses = TrainsHelper.getEngineClasses(interval);
            for (EngineClass engineClass : engineClasses) {
                WeightTableRow row = engineClass.getWeigthTableRowWithMaxSpeed();
                if (row.getSpeed() != UNLIMITED_SPEED) {
                    speed = Math.min(speed, row.getSpeed());
                }
            }
        }

        return speed;
    }

    @Override
    public boolean isLine() {
        return true;
    }

    @Override
    public boolean isNode() {
        return false;
    }

    private interface PenaltySolver {
        int getDecelerationPenalty(int speed);
        int getAccelerationPenalty(int speed);
    }

    /**
     * computes running time for this track.
     *
     * @param train train
     * @param speed speed
     * @param diagram train diagram
     * @param fromSpeed from speed
     * @param toSpeed to speed
     * @return pair running time and speed
     */
    public int computeRunningTime(final Train train, int speed, int fromSpeed, int toSpeed, int addedTime) {
        Scale scale = (Scale) diagram.getAttribute(TrainDiagram.ATTR_SCALE);
        double timeScale = (Double) diagram.getAttribute(TrainDiagram.ATTR_TIME_SCALE);
        final PenaltyTable penaltyTable = diagram.getPenaltyTable();
        PenaltySolver ps = new PenaltySolver() {

            @Override
            public int getDecelerationPenalty(int speed) {
                PenaltyTableRow row = train.getType() != null ? penaltyTable.getRowForSpeedAndCategory(train.getType().getCategory(), speed) : null;
                return row != null ? row.getDeceleration() : 0;
            }

            @Override
            public int getAccelerationPenalty(int speed) {
                PenaltyTableRow row = train.getType() != null ? penaltyTable.getRowForSpeedAndCategory(train.getType().getCategory(), speed) : null;
                return row != null ? row.getAcceleration() : 0;
            }
        };

        TimeConverter converter = train.getTrainDiagram().getTimeConverter();
        Map<String, Object> binding = new HashMap<String, Object>();
        binding.put("speed", speed);
        binding.put("fromSpeed", fromSpeed);
        binding.put("toSpeed", toSpeed);
        binding.put("timeScale", timeScale);
        binding.put("scale", scale.getRatio());
        binding.put("length", length);
        binding.put("addedTime", addedTime);
        binding.put("penaltySolver", ps);
        binding.put("train", train);
        binding.put("converter", converter);
        binding.put("diagram", train.getTrainDiagram());

        Object result = diagram.getTrainsData().getRunningTimeScript().evaluate(binding);
        if (!(result instanceof Number))
            throw new IllegalStateException("Unexpected result: " + result);

        return converter.round(((Number)result).intValue());
    }

    @Override
    public void removeTimeInterval(TimeInterval interval) {
        interval.getTrack().removeTimeInterval(interval);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TIME_INTERVAL_REMOVED, interval));
    }

    @Override
    public String toString() {
        return String.format("%s-%s", from.getAbbr(), to.getAbbr());
    }

    @Override
    public void addTimeInterval(TimeInterval interval) {
        interval.getTrack().addTimeInterval(interval);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TIME_INTERVAL_ADDED, interval));
    }

    @Override
    public void updateTimeInterval(TimeInterval interval) {
        Track track = this.getTrackForInterval(interval);
        if (track == null)
            throw new IllegalStateException("Line doesn't contain interval.");
        track.removeTimeInterval(interval);
        interval.getTrack().addTimeInterval(interval);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TIME_INTERVAL_UPDATED, interval));
    }

    private Track getTrackForInterval(TimeInterval interval) {
        for (Track track : getTracks()) {
            if (track.getTimeIntervalList().contains(interval))
                return track;
        }
        return null;
    }

    @Override
    public Line asLine() {
        return this;
    }

    @Override
    public Node asNode() {
        return null;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public Node getFrom(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? from : to;
    }

    public Node getTo(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? to : from;
    }

    public LineClass getLineClass(TimeIntervalDirection direction) {
        LineClass result = null;
        if (direction == TimeIntervalDirection.BACKWARD)
            result = (LineClass) getAttribute(ATTR_CLASS_BACK);
        if (direction == TimeIntervalDirection.FORWARD || result == null)
            result = (LineClass) getAttribute(ATTR_CLASS);
        return result;
    }

    @Override
    public boolean isEmpty() {
        for (LineTrack track : tracks) {
            if (!track.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns line track with specified number.
     *
     * @param number number
     * @return line track
     */
    public LineTrack getLineTrackByNumber(String number) {
        for (LineTrack track : tracks) {
            if (track.getNumber().equals(number)) {
                return track;
            }
        }
        return null;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public LineTrack findTrackById(String id) {
        for (LineTrack track : getTracks()) {
            if (track.getId().equals(id)) {
                return track;
            }
        }
        return null;
    }

    public void addListener(LineListener listener) {
        this.listenerSupport.addListener(listener);
    }

    public void removeListener(LineListener listener) {
        this.listenerSupport.removeListener(listener);
    }

    void fireTrackAttributeChanged(String attributeName, LineTrack track, Object oldValue, Object newValue) {
        this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange(attributeName, oldValue, newValue), track));
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
     * accepts visitor.
     *
     * @param visitor visitor
     */
    public void accept(TrainDiagramTraversalVisitor visitor) {
        visitor.visit(this);
        for (LineTrack track : tracks) {
            track.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    private Iterable<LineTrack> getIterableByDirection(TimeIntervalDirection direction) {
        if (direction == TimeIntervalDirection.FORWARD) {
            return tracks;
        } else {
            return new Iterable<LineTrack>() {
                @Override
                public Iterator<LineTrack> iterator() {
                    return new Iterator<LineTrack>() {
                        private final ListIterator<LineTrack> i = tracks.listIterator(tracks.size());

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public LineTrack next() {
                            return i.previous();
                        }

                        @Override
                        public boolean hasNext() {
                            return i.hasPrevious();
                        }
                    };
                }
            };
        }
    }
}
