package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Route (consists of route parts - tracks, stations, ...).
 *
 * @author jub
 */
public class Route implements ObjectWithId, Visitable, Iterable<RouteSegment> {

    /** Route parts. */
    private final List<RouteSegment> segments;
    private String name;
    private final String id;
    private boolean netPart;
    private boolean trainRoute;
    private String _cachedToString;

    /**
     * initializes routes.
     */
    private void init() {
    }

    /**
     * Constructor.
     *
     * @param id id
     */
    public Route(String id) {
        init();
        segments = new LinkedList<RouteSegment>();
        this.id = id;
    }

    /**
     * Constructor with name.
     *
     * @param id id
     * @param name name
     */
    public Route(String id, String name) {
        this(id);
        this.name = name;
    }

    /**
     * Copy constructor.
     *
     * @param id id
     * @param route route to be copied
     */
    public Route(String id, Route route) {
        init();
        this.id = id;
        this.name = route.name;
        segments = new LinkedList<RouteSegment>(route.segments);
    }

    /**
     * Constructor.
     *
     * @param id id
     * @param segments route parts
     */
    public Route(String id, RouteSegment... segments) {
        this.id = id;
        this.segments = new LinkedList<RouteSegment>(Arrays.asList(segments));
    }

    /**
     * Constructor.
     *
     * @param id id
     * @param name name
     * @param segments route parts
     */
    public Route(String id, String name, RouteSegment... segments) {
        this(id, segments);
        this.name = name;
    }

    public List<RouteSegment> getSegments() {
        return segments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.clearCached();
        this.name = name;
    }

    private void clearCached() {
        this._cachedToString = null;
    }

    public boolean isNetPart() {
        return netPart;
    }

    public void setNetPart(boolean netPart) {
        this.clearCached();
        this.netPart = netPart;
    }

    public boolean isTrainRoute() {
        return trainRoute;
    }

    public void setTrainRoute(boolean trainRoute) {
        this.clearCached();
        this.trainRoute = trainRoute;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * @param segment checked segment
     * @return if the route contains given segment
     */
    public boolean contains(RouteSegment segment) {
        return segments.contains(segment);
    }

    /**
     * adds route at the end.
     *
     * @param route route to be added
     */
    public void add(Route route) {
        this.clearCached();
        List<RouteSegment> addSegments = route.getSegments();
        if ((segments.size() > 0) && (addSegments.get(0) != segments.get(segments.size() - 1))) {
            throw new IllegalArgumentException("Route to be added doesn't start with appropriate node.");
        }
        ListIterator<RouteSegment> i = addSegments.listIterator((segments.isEmpty()) ? 0 : 1);
        while (i.hasNext()) {
            segments.add(i.next());
        }
    }

    /**
     * checks duplicate nodes in route (true if there are at least one).
     *
     * @return if there are duplicate nodes
     */
    public boolean checkDuplicateNodes() {
        Set<Node> dNodes = new HashSet<Node>();
        for (RouteSegment segment : segments) {
            if (segment instanceof Node) {
                Node node = (Node) segment;
                if (dNodes.contains(node)) {
                    return true;
                } else {
                    dNodes.add(node);
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (_cachedToString == null) {
            StringBuilder builder = new StringBuilder();
            if (name != null && !"".equals(name)) {
                builder.append(name).append(' ');
            }
            builder.append('[');
            boolean first = true;
            for (RouteSegment segment : segments) {
                if (segment.asNode() != null) {
                    if (!first) {
                        builder.append(',');
                    } else {
                        first = false;
                    }
                    builder.append(segment);
                }
            }
            builder.append(']');
            _cachedToString = builder.toString();
        }
        return _cachedToString;
    }

    /**
     * @return iterable which consists only of lines of this route
     */
    public Iterable<Line> lines() {
        return Iterables.filter(segments, Line.class);
    }

    /**
     * @return iterable which consists only of nodes of this route
     */
    public Iterable<Node> nodes() {
        return Iterables.filter(segments, Node.class);
    }

    @Override
    public Iterator<RouteSegment> iterator() {
        return segments.iterator();
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
