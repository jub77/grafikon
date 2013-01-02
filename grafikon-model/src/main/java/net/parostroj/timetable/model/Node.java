package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Node that can consist of several tracks. Each tracks provides its own list
 * of time intervals.
 *
 * @author jub
 */
public class Node implements RouteSegment, AttributesHolder, ObjectWithId, Visitable {

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** ID. */
    private final String id;
    /** Name of the node. */
    private String name;
    /** Abbreviation. */
    private String abbr;
    /** Attributes of the node. */
    private Attributes attributes;
    /** List of node tracks. */
    private List<NodeTrack> tracks;
    /** Node type. */
    private NodeType type;
    /** X position in gui. */
    private int positionX;
    /** Y position in gui. */
    private int positionY;
    private GTListenerSupport<NodeListener, NodeEvent> listenerSupport;
    private AttributesListener attributesListener;

    /**
     * Initialization.
     */
    private void init() {
        tracks = new LinkedList<NodeTrack>();
        this.setAttributes(new Attributes());
        listenerSupport = new GTListenerSupport<NodeListener, NodeEvent>(new GTEventSender<NodeListener, NodeEvent>() {

            @Override
            public void fireEvent(NodeListener listener, NodeEvent event) {
                listener.nodeChanged(event);
            }
        });
    }

    /**
     * creates instance with specified name.
     *
     * @param id id
     * @param diagram train diagram
     * @param type type
     * @param name name
     * @param abbr abbreviation
     */
    Node(String id, TrainDiagram diagram, NodeType type, String name, String abbr) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.abbr = abbr;
        this.diagram = diagram;
        init();
    }
    
    public void addListener(NodeListener listener) {
        this.listenerSupport.addListener(listener);
    }
    
    public void removeListener(NodeListener listener) {
        this.listenerSupport.removeListener(listener);
    }

    /**
     * @return id of the node
     */
    @Override
    public String getId() {
        return id;
    }

    public TrainDiagram getTrainDiagram() {
        return diagram;
    }

    public TimeInterval createTimeInterval(String intervalId, Train train, int start, int stop) {
        int end = start + stop;

        NodeTrack selectedTrack = null;
        TimeInterval interval = new TimeInterval(null, train, this, start, end, null);

        // check which platform is free for adding
        for (NodeTrack nodeTrack : tracks) {
            // skip station tracks with no platform
            if (stop !=0 && train.getType().isPlatform() && !nodeTrack.isPlatform()) {
                continue;
            }
            TimeIntervalResult result = nodeTrack.testTimeInterval(interval);
            if (result.getStatus() == TimeIntervalResult.Status.OK) {
                selectedTrack = nodeTrack;
                break;
            }
        }

        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(0);
        }

        return new TimeInterval(intervalId, train, this, start, end, selectedTrack);
    }

    /**
     * @return the node tracks
     */
    @Override
    public List<NodeTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public void addTrack(NodeTrack track) {
        track.node = this;
        tracks.add(track);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TRACK_ADDED, track));
    }

    public void addTrack(NodeTrack track, int position) {
        track.node = this;
        tracks.add(position, track);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TRACK_ADDED, track));
    }

    public void removeTrack(NodeTrack track) {
        track.node = null;
        tracks.remove(track);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TRACK_REMOVED, track));
    }

    public void moveTrack(NodeTrack track, int position) {
        int oldIndex = tracks.indexOf(track);
        this.moveTrack(oldIndex, position);
    }

    public void moveTrack(int fromIndex, int toIndex) {
        NodeTrack track = tracks.remove(fromIndex);
        tracks.add(toIndex, track);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TRACK_MOVED, track, fromIndex, toIndex));
    }

    public void removeAllTracks() {
        for (NodeTrack track : tracks) {
            track.node = null;
        }
        tracks.clear();
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TRACK_REMOVED));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange("name", oldName, name)));
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        String oldAbbr = this.abbr;
        this.abbr = abbr;
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange("abbr", oldAbbr, abbr)));
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        NodeType oldType = this.type;
        this.type = type;
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange("type", oldType, type)));
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        int oldPos = this.positionX;
        this.positionX = positionX;
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange("positionX", oldPos, positionX)));
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        int oldPos = this.positionY;
        this.positionY = positionY;
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange("positionY", oldPos, positionY)));
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {
            
            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                listenerSupport.fireEvent(new NodeEvent(Node.this, change));
            }
        };
        this.attributes.addListener(attributesListener);
    }

    @Override
    public Object removeAttribute(String key) {
        return this.attributes.remove(key);
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
    public String toString() {
        return getName();
    }

    @Override
    public void removeTimeInterval(TimeInterval interval) {
        interval.getTrack().removeTimeInterval(interval);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TIME_INTERVAL_REMOVED, interval));
    }

    @Override
    public void addTimeInterval(TimeInterval interval) {
        interval.getTrack().addTimeInterval(interval);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TIME_INTERVAL_ADDED, interval));
    }

    @Override
    public void updateTimeInterval(TimeInterval interval) {
        Track track = this.getTrackForInterval(interval);
        if (track == null)
            throw new IllegalStateException("Node doesn't contain interval.");
        track.removeTimeInterval(interval);
        interval.getTrack().addTimeInterval(interval);
        this.listenerSupport.fireEvent(new NodeEvent(this, GTEventType.TIME_INTERVAL_UPDATED, interval));
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
        return null;
    }

    @Override
    public Node asNode() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        for (NodeTrack track : tracks) {
            if (!track.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns node track with specified number.
     * 
     * @param number number
     * @return node track
     */
    public NodeTrack getNodeTrackByNumber(String number) {
        for (NodeTrack track : tracks) {
            if (track.getNumber().equals(number)) {
                return track;
            }
        }
        return null;
    }

    /**
     * returns set of overlapping intervals for all node tracks for given
     * time interval.
     * 
     * @param interval checked interval
     * @return set of overlapping time intervals
     */
    public Set<TimeInterval> getOverlappingTimeIntervals(TimeInterval interval) {
        Set<TimeInterval> out = null;
        for (NodeTrack track : tracks) {
            TimeIntervalResult result = track.testTimeIntervalOI(interval);
            if (result.getStatus() == TimeIntervalResult.Status.OVERLAPPING) {
                if (out == null) {
                    out = new HashSet<TimeInterval>(result.getOverlappingIntervals());
                } else {
                    out.addAll(result.getOverlappingIntervals());
                }
            }
        }
        if (out == null) {
            return Collections.emptySet();
        } else {
            return out;
        }
    }

    @Override
    public NodeTrack findTrackById(String id) {
        for (NodeTrack track : getTracks()) {
            if (track.getId().equals(id)) {
                return track;
            }
        }
        return null;
    }

    void fireTrackAttributeChanged(String attributeName, NodeTrack track, Object oldValue, Object newValue) {
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange(attributeName, oldValue, newValue), track));
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
     * accepts traversal visitor.
     *
     * @param visitor traversal visitor
     */
    public void accept(TrainDiagramTraversalVisitor visitor) {
        visitor.visit(this);
        for (NodeTrack track : tracks) {
            track.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    @Override
    public boolean isLine() {
        return false;
    }

    @Override
    public boolean isNode() {
        return true;
    }
}
