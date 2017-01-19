package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.Observable;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.ListenableUndirectedGraph;

/**
 * Special class for net.
 *
 * @author jub
 */
public class Net implements ObjectWithId, Visitable, TrainDiagramPart, Observable {

    private final String id;
    private final TrainDiagram diagram;
    private final ItemWithIdList<LineClass> lineClasses;
    private final ItemWithIdSet<Region> regions;
    private final ListenableUndirectedGraph<Node, Line> netDelegate;
    private final ValidationListener listener;
    private final ListenerSupport listenerSupport;
    private final ListenerSupport listenerSupportAll;
    private final List<ItemWithIdIterable<? extends ObjectWithId>> itemLists;

    /**
     * Constructor.
     */
    public Net(String id, TrainDiagram diagram) {
        this.itemLists = new LinkedList<>();
        this.netDelegate = new ListenableUndirectedGraph<>(Line.class);
        this.lineClasses = new ItemWithIdListImpl<>(
                (type, item, newIndex, oldIndex) -> fireCollectionEventObservable(type, item, newIndex, oldIndex));
        this.regions = new ItemWithIdSetImpl<>(
                (type, item) -> fireCollectionEvent(type, item, null, null));
        this.listenerSupport = new ListenerSupport();
        this.listenerSupportAll = new ListenerSupport();
        this.listener = event -> this.fireNestedEvent(event);
        this.id = id;
        this.diagram = diagram;
        Collections.addAll(itemLists, lineClasses, regions);
    }

    @Override
    public String getId() {
        return id;
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

    public void addLine(Node from, Node to, Line line) {
        netDelegate.addEdge(from, to, line);
        this.fireEvent(new Event(this, Event.Type.ADDED, line));
        // adapt from and to straight
        Iterator<NodeTrack> fromS = this.straight(from).iterator();
        Iterator<NodeTrack> toS = this.straight(to).iterator();
        for (LineTrack lt : line.getTracks()) {
            if (fromS.hasNext()) {
                lt.setFromStraightTrack(fromS.next());
            }
            if (toS.hasNext()) {
                lt.setToStraightTrack(toS.next());
            }
        }
        line.addListener(listener);
    }

    private List<NodeTrack> straight(Node node) {
        LinkedList<NodeTrack> result = new LinkedList<>();
        for (NodeTrack track : node.getTracks()) {
            if (track.getAttributes().getBool(NodeTrack.ATTR_NODE_TRACK_STRAIGHT)) {
                result.add(track);
            }
        }
        return result;
    }

    public void removeLine(Line line) {
        netDelegate.removeEdge(line);
        this.fireEvent(new Event(this, Event.Type.REMOVED, line));
        line.removeListener(listener);
    }

    public List<Line> getRoute(Node from, Node to) {
        return DijkstraShortestPath.findPathBetween(netDelegate, from, to);
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

    public void addAllEventListener(Listener listener) {
        this.listenerSupportAll.addListener(listener);
    }

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
        if (getId().equals(id)) {
            return this;
        }
        ObjectWithId object;
        for (ItemWithIdIterable<? extends ObjectWithId> itemList : itemLists) {
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
        return object;
    }

    private void fireEvent(Event.Type type, Object item, Integer newIndex, Integer oldIndex) {
        Event event = new Event(Net.this, type, item, ListData.createData(oldIndex, newIndex));
        Net.this.fireEvent(event);
    }

    private void fireCollectionEvent(Event.Type type, ItemListObject item, Integer newIndex, Integer oldIndex) {
        fireEvent(type, item, newIndex, oldIndex);
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

    protected void fireCollectionEventObservable(Event.Type type, Observable item, Integer newIndex, Integer oldIndex) {
        fireEvent(type, item, newIndex, oldIndex);
        switch (type) {
            case ADDED:
                item.addListener(listener);
                break;
            case REMOVED:
                item.removeListener(listener);
                break;
            default: // nothing
                break;
        }
    }
}
