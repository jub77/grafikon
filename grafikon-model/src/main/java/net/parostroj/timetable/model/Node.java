package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.ls.ModelVersion;
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
public class Node extends NetSegmentImpl<NodeTrack> implements Visitable, TrainDiagramPart, ObjectWithVersion {

    public static final String IP_NEW_SIGNALS = "new.signals";

    public static final String ATTR_CONTROL_STATION = "control.station";
    public static final String ATTR_INTERLOCKING_PLANT = "interlocking.plant";
    public static final String ATTR_TRAPEZOID_SIGN = "trapezoid.sign";
    public static final String ATTR_LENGTH = "length";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_ABBR = "abbr";
    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_FREIGHT_COLORS = "freight.colors";
    public static final String ATTR_REGIONS = "regions";
    public static final String ATTR_CENTER_OF_REGIONS = "center.regions";
    public static final String ATTR_NOT_STRAIGHT_SPEED = "not.straight.speed";
    public static final String ATTR_SPEED = "speed";
    public static final String ATTR_COMPANY = "company";
    public static final String ATTR_TELEPHONE = "telephone";
    public static final String ATTR_FREIGHT_CAPACITY = "freight.capacity";
    public static final String ATTR_FREIGHT_NOTE = "freight.note";
    public static final String ATTR_VERSION = "version";

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
                removeFromOldRegions(oldR, newR);
                addToNewRegions(oldR, newR);
            }
        });
        this.setLocation(new Location(0, 0));
    }

    private void addToNewRegions(Collection<Region> oldR, Collection<Region> newR) {
        if (newR != null) {
            for (Region r : newR) {
                if ((oldR == null || !oldR.contains(r)) && r.getDiagram() == diagram) {
                    r.addNode(Node.this);
                }
            }
        }
    }

    private void removeFromOldRegions(Collection<Region> oldR, Collection<Region> newR) {
        if (oldR != null) {
            for (Region r : oldR) {
                if ((newR == null || !newR.contains(r)) && r.getDiagram() == diagram) {
                    r.removeNode(Node.this);
                }
            }
        }
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

    @Override
    public ModelVersion getVersion() {
        return getAttribute(ATTR_VERSION, ModelVersion.class, ModelVersion.initialModelVersion());
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

    public String getTelephone() {
        return this.getAttribute(ATTR_TELEPHONE, String.class);
    }

    public Integer getFreightCapacity() {
        return this.getAttribute(ATTR_FREIGHT_CAPACITY, Integer.class);
    }

    public LocalizedString getFreightNote() {
        return this.getAttribute(ATTR_FREIGHT_NOTE, LocalizedString.class);
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
                    break;
                default: // nothing
                    break;
            }
        }
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
