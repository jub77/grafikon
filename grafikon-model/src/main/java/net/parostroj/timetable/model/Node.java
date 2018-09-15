package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.Event;
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
public class Node extends RouteSegmentImpl<NodeTrack> implements RouteSegment<NodeTrack>, AttributesHolder,
        ObjectWithId, Visitable, NodeAttributes, TrainDiagramPart {

    public enum Side { LEFT, RIGHT }

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Attributes of the node. */
    private Attributes attributes;
    /** Location of node. */
    private Location location;
    /** Node ports. */
    private final ItemSet<NodePort> ports;

    // views on regions
    private final RegionHierarchy regionHierarchy;
    private final RegionHierarchy centerRegionHierarchy;

    /**
     * Initialization.
     */
    @SuppressWarnings("unchecked")
    private void init() {
        location = new Location(0, 0);
        attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(Node.this, change)));
        attributes.addListener((attrs, change) -> {
            if (change.checkName(ATTR_REGIONS)) {
                Collection<Region> oldR = (Collection<Region>) change.getOldValue();
                Collection<Region> newR = (Collection<Region>) change.getNewValue();
                if (oldR != null) {
                    for (Region r : oldR) {
                        if (newR == null || !newR.contains(r)) {
                            r.removeNode(Node.this);
                        }
                    }
                }
                if (newR != null) {
                    for (Region r : newR) {
                        if (oldR == null || !oldR.contains(r)) {
                            r.addNode(Node.this);
                        }
                    }
                }
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
        super(id);
        this.diagram = diagram;
        init();
        this.setName(name);
        this.setType(type);
        this.setAbbr(abbr);
        regionHierarchy = new NodeRegionHierarchy(false);
        centerRegionHierarchy = new NodeRegionHierarchy(true);
        this.ports = new ItemSetImpl<>(this::fireCollectionEvent);
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
        return this.attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        this.attributes.setRemove(ATTR_NAME, name);
    }

    public String getAbbr() {
        return this.attributes.get(ATTR_ABBR, String.class);
    }

    public void setAbbr(String abbr) {
        this.attributes.setRemove(ATTR_ABBR, abbr);
    }

    public NodeType getType() {
        return this.attributes.get(ATTR_TYPE, NodeType.class);
    }

    public void setType(NodeType type) {
        this.attributes.setRemove(ATTR_TYPE, type);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (!ObjectsUtil.compareWithNull(location, this.location)) {
            Location oldLocation = this.location;
            this.location = location;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_LOCATION, oldLocation, location)));
        }
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
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
                    out = new HashSet<>(result.getOverlappingIntervals());
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
        return this.getAttribute(ATTR_NOT_STRAIGHT_SPEED, Integer.class);
    }

    public Integer getSpeed() {
        return this.getAttribute(ATTR_SPEED, Integer.class);
    }

    public Integer getLength() {
        return this.getAttribute(ATTR_LENGTH, Integer.class);
    }

    public Company getCompany() {
        return this.getAttribute(ATTR_COMPANY, Company.class);
    }

    public Set<Region> getCenterRegions() {
    	return this.getAttributeAsSet(ATTR_CENTER_OF_REGIONS, Region.class, Collections.emptySet());
    }

    public RegionHierarchy getCenterRegionHierarchy() {
        return centerRegionHierarchy;
    }

    public boolean isCenterOfRegions() {
        Set<?> regions = this.getAttribute(ATTR_CENTER_OF_REGIONS, Set.class);
        return regions != null && !regions.isEmpty();
    }

    public Set<Region> getRegions() {
    	return this.getAttributeAsSet(ATTR_REGIONS, Region.class, Collections.emptySet());
    }

    public RegionHierarchy getRegionHierarchy() {
        return regionHierarchy;
    }

    public Set<FreightColor> getFreightColors() {
        return this.getAttributeAsSet(ATTR_FREIGHT_COLORS, FreightColor.class, Collections.emptySet());
    }

    public void setFreightColors(Set<FreightColor> freightColors) {
        getAttributes().setRemove(ATTR_FREIGHT_COLORS, ObjectsUtil.checkEmpty(freightColors));
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

    public ItemSet<NodePort> getPorts() {
        return ports;
    }

    void fireCollectionEvent(Event.Type type, Object item) {
        this.fireEvent(new Event(this, type, item));
    }

    void fireCollectionEventListObject(Event.Type type, ItemCollectionObject item, Integer from, Integer to) {
        fireCollectionEvent(type, item);
        switch (type) {
            case ADDED:
                item.added();
                break;
            case REMOVED:
                item.removed();
                break;
            default: // nothing
                break;
        }
    }

    protected void fireEvent(Event event) {
        listenerSupport.fireEvent(event);
    }

    protected NodePort createNodePort(final Side side) {
        return new NodePortImpl(this, side);
    }

    private class NodeRegionHierarchy extends RegionHierarchyImpl {
        private final boolean center;

        public NodeRegionHierarchy(boolean center) {
            this.center = center;
        }

        @Override
        public Set<Region> getRegions() {
            return center ? Node.this.getCenterRegions() : Node.this.getRegions();
        }
    }
}
