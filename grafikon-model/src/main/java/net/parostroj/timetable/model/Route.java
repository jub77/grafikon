package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.utils.TransformUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Route (consists of route parts - tracks, stations, ...).
 *
 * @author jub
 */
public class Route implements ObjectWithId, Visitable, Iterable<RouteSegment<? extends Track>>, TrainDiagramPart {

    private final TrainDiagram diagram;
    private final String id;
    /** Route parts. */
    private List<RouteSegment<? extends Track>> segments;
    private String name;
    private boolean netPart;

    /**
     * Constructor.
     */
    public Route(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        segments = new LinkedList<>();
    }

    /**
     * Constructor with name.
     */
    public Route(String id, TrainDiagram diagram, String name) {
        this(id, diagram);
        this.name = name;
    }

    /**
     * Copy constructor.
     */
    public Route(String id, Route route) {
        this(id, route.diagram, route.name);
        segments = new LinkedList<>(route.segments);
    }

    @SafeVarargs
    public Route(String id, TrainDiagram diagram, RouteSegment<? extends Track>... segments) {
        this(id, diagram);
        this.segments = new LinkedList<>(Arrays.asList(segments));
    }

    @SafeVarargs
    public Route(String id, TrainDiagram diagram, String name, RouteSegment<? extends Track>... segments) {
        this(id, diagram, segments);
        this.name = name;
    }

    public List<RouteSegment<? extends Track>> getSegments() {
        return segments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNetPart() {
        return netPart;
    }

    public void setNetPart(boolean netPart) {
        this.netPart = netPart;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    /**
     * @param segment checked segment
     * @return if the route contains given segment
     */
    public boolean contains(RouteSegment<? extends Track> segment) {
        return segments.contains(segment);
    }

    /**
     * adds route at the end.
     *
     * @param route route to be added
     */
    public void add(Route route) {
        List<RouteSegment<?>> addSegments = route.getSegments();
        if (!segments.isEmpty() && (addSegments.get(0) != segments.get(segments.size() - 1))) {
            throw new IllegalArgumentException("Route to be added doesn't start with appropriate node.");
        }
        ListIterator<RouteSegment<?>> i = addSegments.listIterator((segments.isEmpty()) ? 0 : 1);
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
        Set<Node> dNodes = new HashSet<>();
        for (RouteSegment<?> segment : segments) {
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
        return TransformUtil.transformRoute(this);
    }

    /**
     * @return iterable which consists only of lines of this route
     */
    public Iterable<Line> getLines() {
        return Iterables.filter(segments, Line.class);
    }

    public int getLinesCount() {
        return segments.size() / 2;
    }

    /**
     * @return iterable which consists only of nodes of this route
     */
    public Iterable<Node> getNodes() {
        return Iterables.filter(segments, Node.class);
    }

    public int getNodesCount() {
        return segments.size() / 2 + 1;
    }

    public Node getLast() {
        return (Node) segments.get(segments.size() - 1);
    }

    public Node getFirst() {
        return (Node) segments.get(0);
    }

    @Override
    public Iterator<RouteSegment<? extends Track>> iterator() {
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
