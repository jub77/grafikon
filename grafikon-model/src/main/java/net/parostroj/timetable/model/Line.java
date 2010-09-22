package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.LineEvent;
import net.parostroj.timetable.model.events.LineListener;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Track between two route points.
 *
 * @author jub
 */
public class Line implements RouteSegment, AttributesHolder, ObjectWithId, Visitable {

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** ID. */
    private final String id;
    /** Length in mm. */
    private int length;
    /** List of node tracks. */
    private List<LineTrack> tracks;
    /** Top speed for the track. */
    private int topSpeed = UNLIMITED_SPEED;
    /** Unlimited spedd. */
    public static final int UNLIMITED_SPEED = -1;
    /** No speed constant. */
    public static final int NO_SPEED = UNLIMITED_SPEED;
    /** Attributes. */
    private Attributes attributes;
    /** Starting point. */
    private Node from;
    /** Ending point. */
    private Node to;
    private GTListenerSupport<LineListener, LineEvent> listenerSupport;

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
        attributes = new Attributes();
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

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
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

    public TimeInterval createTimeInterval(String intervalId, Train train, int start, TimeIntervalDirection direction, int speed, int fromSpeed, int toSpeed) {
        int computedTime = this.computeRunningTime(train, speed, fromSpeed, toSpeed);
        int end = start + computedTime;

        LineTrack selectedTrack = null;
        TimeInterval interval = new TimeInterval(null, train, this, start, end, speed, direction, null);

        // check which track is free for adding
        for (LineTrack lineTrack : tracks) {
            TimeIntervalResult result = lineTrack.testTimeInterval(interval);
            if (result.getStatus() == TimeIntervalResult.Status.OK) {
                selectedTrack = lineTrack;
                break;
            }
        }

        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(0);
        }

        return new TimeInterval(intervalId, train, this, start, end, speed, direction, selectedTrack);
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

    public int computeSpeed(Train train, int prefferedSpeed) {
        int speed;
        if (prefferedSpeed != NO_SPEED) {
            speed = Math.min(prefferedSpeed, train.getTopSpeed());
        } else {
            // apply max train speed
            speed = train.getTopSpeed();
        }

        // apply track speed limit
        if (this.topSpeed != UNLIMITED_SPEED) {
            speed = Math.min(speed, this.topSpeed);
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
    public int computeRunningTime(final Train train, int speed, int fromSpeed, int toSpeed) {
        Scale scale = (Scale) diagram.getAttribute("scale");
        double timeScale = (Double) diagram.getAttribute("time.scale");
        final PenaltyTable penaltyTable = diagram.getPenaltyTable();
        PenaltySolver ps = new PenaltySolver() {

            @Override
            public int getDecelerationPenalty(int speed) {
                return penaltyTable.getRowForSpeedAndCategory(train.getType().getCategory(), speed).getDeceleration();
            }

            @Override
            public int getAccelerationPenalty(int speed) {
                return penaltyTable.getRowForSpeedAndCategory(train.getType().getCategory(), speed).getAcceleration();
            }
        };

        Map<String, Object> binding = new HashMap<String, Object>();
        binding.put("speed", speed);
        binding.put("fromSpeed", fromSpeed);
        binding.put("toSpeed", toSpeed);
        binding.put("timeScale", timeScale);
        binding.put("scale", scale.getRatio());
        binding.put("length", length);
        binding.put("penaltySolver", ps);

        Object result = diagram.getTrainsData().getRunningTimeScript().evaluate(binding);
        if (!(result instanceof Integer))
            throw new IllegalStateException("Unexpected result: " + result);

        return ((Integer)result).intValue();
    }

    @Override
    public void removeTimeInterval(TimeInterval interval) {
        interval.getTrack().removeTimeInterval(interval);
        this.listenerSupport.fireEvent(new LineEvent(this, GTEventType.TIME_INTERVAL_REMOVED, interval));
    }

    @Override
    public String toString() {
        return from.getAbbr() + "-" + to.getAbbr() + " <" + length + "," + topSpeed + ">";
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
        Object oldValue = attributes.get(key);
        attributes.put(key, value);
        this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange(key, oldValue, value)));
    }

    @Override
    public Object removeAttribute(String key) {
        Object returnValue = attributes.remove(key);
        if (returnValue != null)
            this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange(key, returnValue, null)));
        return returnValue;
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
}
