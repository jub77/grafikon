package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.ObservableObject;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.SimpleGraph;

/**
 * Special class for net.
 *
 * @author jub
 */
public class Net implements Visitable, TrainDiagramPart, ObservableObject, CompounedObservable {

    private final TrainDiagram diagram;
    private final ItemWithIdList<LineClass> lineClasses;
    private final ItemWithIdSet<Region> regions;
    private final DefaultListenableGraph<Node, Line> netDelegate;
    private final SystemListener listener;
    private final ListenerSupport listenerSupport;
    private final ListenerSupport listenerSupportAll;
    private final Iterable<ItemWithIdIterable<?>> itemLists;

    /**
     * Constructor.
     */
    public Net(TrainDiagram diagram) {
        this.netDelegate = new DefaultListenableGraph<>(new SimpleGraph<>(Line.class));
        this.lineClasses = new ItemWithIdListImpl<>(this::fireEvent);
        this.regions = new ItemWithIdSetImpl<>(this::fireEvent);
        this.listenerSupport = new ListenerSupport();
        this.listenerSupportAll = new ListenerSupport();
        this.listener = this::fireNestedEvent;
        this.diagram = diagram;
        this.itemLists = List.of(lineClasses, regions);
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public Tuple<Node> getNodes(Line track) {
        return new Tuple<>(netDelegate.getEdgeSource(track),netDelegate.getEdgeTarget(track));
    }

    public Set<Node> getNodes() {
        return netDelegate.vertexSet();
    }

    public void addNode(Node node) {
        netDelegate.addVertex(node);
        this.fireEvent(new Event(this, Event.Type.ADDED, node));
        node.addListener(listener);
    }

    public void removeNode(Node node) {
        List<Line> lines = List.copyOf(netDelegate.edgesOf(node));
        lines.forEach(this::removeLine);
        netDelegate.removeVertex(node);
        this.fireEvent(new Event(this, Event.Type.REMOVED, node));
        node.removeListener(listener);
    }

    public Set<Line> getLines() {
        return netDelegate.edgeSet();
    }

    public Set<Line> getLinesOf(Node node) {
        return netDelegate.edgesOf(node);
    }

    public Line getLine(Node node1, Node node2) {
        return netDelegate.getEdge(node1, node2);
    }

    Node getFrom(Line line) {
        return netDelegate.containsEdge(line) ? netDelegate.getEdgeSource(line) : null;
    }

    Node getTo(Line line) {
        return netDelegate.containsEdge(line) ? netDelegate.getEdgeTarget(line) : null;
    }

    public void addLine(Line line, Node from, Node to) {
        netDelegate.addEdge(from, to, line);
        this.fireEvent(new Event(this, Event.Type.ADDED, line));
        line.addListener(listener);
    }

    public void removeLine(Line line) {
        line.getTracks().clear();
        netDelegate.removeEdge(line);
        this.fireEvent(new Event(this, Event.Type.REMOVED, line));
        line.removeListener(listener);
    }

    public List<Line> getRoute(Node from, Node to) {
        GraphPath<Node, Line> path = DijkstraShortestPath.findPathBetween(netDelegate, from, to);
        return path == null ? null : path.getEdgeList();
    }

    public ItemWithIdList<LineClass> getLineClasses() {
        return lineClasses;
    }

    public ItemWithIdSet<Region> getRegions() {
        return regions;
    }

    public Node getNodeById(String id) {
        for (Node node : netDelegate.vertexSet()) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    public Node getNodeByName(String name) {
        for (Node node : netDelegate.vertexSet()) {
            if (ObjectsUtil.compareWithNull(node.getName(), name)) {
                return node;
            }
        }
        return null;
    }

    public Node getNodeByAbbr(String abbr) {
        for (Node node : netDelegate.vertexSet()) {
            if (ObjectsUtil.compareWithNull(node.getAbbr(), abbr)) {
                return node;
            }
        }
        return null;
    }

    public Line getLineById(String id) {
        for (Line line : netDelegate.edgeSet()) {
            if (line.getId().equals(id)) {
                return line;
            }
        }
        return null;
    }

    public Graph<Node, Line> getGraph() {
        return netDelegate;
    }

    @Override
    public void addListener(Listener listener) {
        this.listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listenerSupport.removeListener(listener);
    }

    @Override
    public void addAllEventListener(Listener listener) {
        this.listenerSupportAll.addListener(listener);
    }

    @Override
    public void removeAllEventListener(Listener listener) {
        this.listenerSupportAll.removeListener(listener);
    }

    void fireNestedEvent(Event event) {
        this.listenerSupportAll.fireEvent(event);
    }

    private void fireEvent(Event event) {
        this.listenerSupport.fireEvent(event);
        this.listenerSupportAll.fireEvent(event);
    }

    @Override
    public String toString() {
        return String.format("Net[Nodes:%d,Lines:%s", getNodes().size(), getLines().size());
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
        for (Line line : getLines()) {
            line.accept(visitor);
        }
        for (Node node : getNodes()) {
            node.accept(visitor);
        }
        for (LineClass lineClass : lineClasses) {
            lineClass.accept(visitor);
        }
        for (Region region : regions) {
            region.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    public ObjectWithId getObjectById(String id) {
        ObjectWithId object;
        for (ItemWithIdIterable<?> itemList : itemLists) {
            object = itemList.getById(id);
            if (object != null) {
                return object;
            }
        }
        object = getLineById(id);
        if (object != null) {
            return object;
        }
        object = getNodeById(id);
        if (object != null) {
            return object;
        }
        for (Node node : getNodes()) {
            object = node.getConnectors().getById(id);
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    private void fireEvent(Event.Type type, Object item) {
        this.fireEvent(type, item, null, null);
    }

    private void fireEvent(Event.Type type, Object item, Integer newIndex, Integer oldIndex) {
        Event event = new Event(Net.this, type, item, ListData.createData(oldIndex, newIndex));
        this.fireEvent(event);
        if (item instanceof ItemCollectionObject o) {
            switch (type) {
                case ADDED -> o.added();
                case REMOVED -> o.removed();
                default -> {}
            }
        }
        if (item instanceof ObservableObject o) {
            switch (type) {
                case ADDED -> o.addListener(listener);
                case REMOVED -> o.removeListener(listener);
                default -> {}
            }
        }
    }
}
