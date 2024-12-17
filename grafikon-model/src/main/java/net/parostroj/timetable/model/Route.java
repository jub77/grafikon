package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.Iterables;

import com.google.common.collect.Lists;
import net.parostroj.timetable.utils.TransformUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Route (consists of route parts - tracks, stations, ...).
 *
 * @author jub
 */
public class Route implements ObjectWithId, Visitable, AttributesHolder, Iterable<RouteSegment>, TrainDiagramPart {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_NET_PART = "net.part";

    private final TrainDiagram diagram;
    private final String id;
    private final Attributes attributes;
    /** Route parts. */
    private List<RouteSegment> segments;

    /**
     * Constructor.
     */
    public Route(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes();
        this.segments = new LinkedList<>();
    }

    /**
     * Copy constructor.
     */
    public Route(String id, Route route) {
        this(id, route.diagram);
        this.setName(route.getName());
        segments = new LinkedList<>(route.segments);
    }

    public Route(String id, TrainDiagram diagram, Iterable<RouteSegment> segments) {
        this(id, diagram);
        this.segments = Lists.newLinkedList(segments);
    }

    public Route(String id, TrainDiagram diagram, RouteSegment... segments) {
        this(id, diagram);
        this.segments = new LinkedList<>(Arrays.asList(segments));
    }

    public Route(String id, TrainDiagram diagram, String name, RouteSegment... segments) {
        this(id, diagram, segments);
        this.setName(name);
    }

    public List<RouteSegment> getSegments() {
        return segments;
    }

    public String getName() {
        return this.attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        this.attributes.setRemove(ATTR_NAME, name);
    }

    public boolean isNetPart() {
        return this.attributes.getBool(ATTR_NET_PART);
    }

    public void setNetPart(boolean netPart) {
        this.attributes.setBool(ATTR_NET_PART, netPart);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
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
        List<RouteSegment> addSegments = route.getSegments();
        if (!segments.isEmpty() && (addSegments.getFirst() != segments.getLast())) {
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
        Set<Node> dNodes = new HashSet<>();
        for (RouteSegment segment : segments) {
            if (segment instanceof Node node) {
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
        return (Node) segments.getLast();
    }

    public Node getFirst() {
        return (Node) segments.getFirst();
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
