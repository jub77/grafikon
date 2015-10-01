package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Node that can consist of several tracks. Each tracks provides its own list
 * of time intervals.
 *
 * @author jub
 */
public class Node extends RouteSegmentImpl<NodeTrack> implements RouteSegment, AttributesHolder, ObjectWithId, Visitable, NodeAttributes, TrainDiagramPart {

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Name of the node. */
    private String name;
    /** Abbreviation. */
    private String abbr;
    /** Attributes of the node. */
    private Attributes attributes;
    /** Node type. */
    private NodeType type;
    /** Location of node. */
    private Location location;
    private GTListenerSupport<NodeListener, NodeEvent> listenerSupport;

    /**
     * Initialization.
     */
    private void init() {
        location = new Location(0, 0);
        attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new NodeEvent(Node.this, change)));
        listenerSupport = new GTListenerSupport<NodeListener, NodeEvent>(
                (listener, event) -> listener.nodeChanged(event));
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
        super(id);
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

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public NodeTrack selectTrack(TimeInterval interval, NodeTrack preselectedTrack) {
        NodeTrack selectedTrack = preselectedTrack;
        if (selectedTrack == null || selectedTrack.testTimeInterval(interval).getStatus() != TimeIntervalResult.Status.OK) {
            // check which platform is free for adding
            for (NodeTrack nodeTrack : tracks) {
                // skip station tracks with no platform
                TrainType trainType = interval.getTrain().getType();
                if (interval.getLength() != 0 && trainType != null && trainType.isPlatform() && !nodeTrack.isPlatform()) {
                    continue;
                }
                TimeIntervalResult result = nodeTrack.testTimeInterval(interval);
                if (result.getStatus() == TimeIntervalResult.Status.OK) {
                    selectedTrack = nodeTrack;
                    break;
                }
            }
        }
        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(0);
        }
        return selectedTrack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        if (!ObjectsUtil.compareWithNull(abbr, this.abbr)) {
            String oldAbbr = this.abbr;
            this.abbr = abbr;
            this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange(ATTR_ABBR, oldAbbr, abbr)));
        }
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        if (type != this.type) {
            NodeType oldType = this.type;
            this.type = type;
            this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange(ATTR_TYPE, oldType, type)));
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (!ObjectsUtil.compareWithNull(location, this.location)) {
            Location oldLocation = this.location;
            this.location = location;
            this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange(ATTR_LOCATION, oldLocation, location)));
        }
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
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
    public Line asLine() {
        return null;
    }

    @Override
    public Node asNode() {
        return this;
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

    public Integer getNotStraightSpeed() {
        return this.getAttribute(Node.ATTR_NOT_STRAIGHT_SPEED, Integer.class);
    }

    public Integer getSpeed() {
        return this.getAttribute(Node.ATTR_SPEED, Integer.class);
    }

    public Integer getLength() {
        return this.getAttribute(Node.ATTR_LENGTH, Integer.class);
    }

    @Override
    protected void fireTimeIntervalEvent(TimeInterval interval, GTEventType eventType) {
        this.listenerSupport.fireEvent(new NodeEvent(this, eventType, interval));
    }

    @Override
    protected void fireTrackAttributeChanged(String attributeName, Track track, Object oldValue, Object newValue) {
        this.listenerSupport.fireEvent(new NodeEvent(this, new AttributeChange(attributeName, oldValue, newValue), (NodeTrack) track));
    }

    @Override
    protected void fireTrackEvent(Track track, GTEventType eventType, Integer from, Integer to) {
        if (from == null && to == null) {
            this.listenerSupport.fireEvent(new NodeEvent(this, eventType, (NodeTrack) track));
        } else {
            this.listenerSupport.fireEvent(new NodeEvent(this, eventType, (NodeTrack) track, from, to));
        }
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
