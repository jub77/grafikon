package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class Node extends RouteSegmentImpl<NodeTrack> implements RouteSegment<NodeTrack>,
        AttributesHolder, ObjectWithId, Visitable, NodeAttributes, TrainDiagramPart {

    public enum Side {
        LEFT {
            @Override
            public Side opposite() {
                return RIGHT;
            }
        },
        RIGHT {
            @Override
            public Side opposite() {
                return LEFT;
            }
        };
        public abstract Side opposite();
    }

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Attributes of the node. */
    private Attributes attributes;
    /** Track connectors. */
    private final TrackConnectors connectors;

    // views on regions
    private final RegionHierarchy regionHierarchy;
    private final RegionHierarchy centerRegionHierarchy;

    /**
     * Initialization.
     */
    @SuppressWarnings("unchecked")
    private void init() {
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
        this.setLocation(new Location(0, 0));
    }

    /**
     * creates instance with specified name.
     *
     * @param id id
     * @param diagram train diagram
     */
    Node(String id, TrainDiagram diagram) {
        super(id);
        this.diagram = diagram;
        init();
        regionHierarchy = new NodeRegionHierarchy(false);
        centerRegionHierarchy = new NodeRegionHierarchy(true);
        this.connectors = new TrackConnectorsImpl(this::fireCollectionEvent);
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    NodeTrack selectTrack(TimeInterval interval, NodeTrack preselectedTrack, LineTrack fromTrack,
            Collection<? extends Track> toTracks) {
        NodeTrack selectedTrack = this.checkSelection(preselectedTrack, interval);
        RouteTracksComputation rtc = RouteTracksComputation.getDefaultInstance();
        Set<NodeTrack> trackSet = rtc.getAvailableNodeTracks(
                fromTrack != null ? Collections.singletonList(fromTrack) : Collections.emptySet(),
                interval.getOwnerAsNode(), toTracks);
        List<NodeTrack> tracks = rtc.toTrackList(interval, trackSet, NodeTrack.class);
        if (!trackSet.contains(selectedTrack)) {
            selectedTrack = null;
        }
        if (selectedTrack == null && !interval.isFirst()) {
            // prefer straight
            LineTrack lineTrack = (LineTrack) interval.getPreviousTrainInterval().getTrack();
            selectedTrack = this.getConnectors().getForLineTrack(lineTrack)
                    .flatMap(c -> c.getStraightNodeTrack())
                    .filter(t -> this.checkSelection(t, interval) != null)
                    .filter(trackSet::contains)
                    .orElse(null);
        }
        if (selectedTrack == null) {
            for (NodeTrack nodeTrack : tracks) {
                TrainType trainType = interval.getTrain().getType();
                if (interval.getLength() != 0 && trainType != null && trainType.isPlatform()
                        && !nodeTrack.isPlatform()) {
                    // skip station tracks with no platform (if needed)
                    continue;
                }
                selectedTrack = this.checkSelection(nodeTrack, interval);
                if (selectedTrack != null) {
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

    private NodeTrack checkSelection(NodeTrack track, TimeInterval interval) {
        return track != null
                && track.testTimeInterval(interval).getStatus() == TimeIntervalResult.Status.OK
                        ? track
                        : null;
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
        return this.attributes.get(ATTR_LOCATION, Location.class);
    }

    public void setLocation(Location location) {
        this.attributes.setRemove(ATTR_LOCATION, location);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return getName();
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
        for (TrackConnector connector : connectors) {
            connector.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    public TrackConnectors getConnectors() {
        return connectors;
    }

    void fireCollectionEvent(Event.Type type, Object item) {
        this.fireEvent(new Event(this, type, item));
        if (item instanceof ItemCollectionObject) {
            switch (type) {
                case ADDED:
                    ((ItemCollectionObject) item).added();
                    break;
                case REMOVED:
                    ((ItemCollectionObject) item).removed();
                default: // nothing
                    break;
            }
        }
    }

    void fireCollectionEventListObject(Event.Type type, Object item, Integer from, Integer to) {
        fireCollectionEvent(type, item);
    }

    protected void fireEvent(Event event) {
        listenerSupport.fireEvent(event);
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
