package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.model.events.*;
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
public class Net implements ObjectWithId, Visitable {

    private final String id;
    private final List<LineClass> lineClasses;
    private final ListenableUndirectedGraph<Node, Line> netDelegate;
    private final GTListenerNetImpl listener;
    private final GTListenerSupport<NetListener, NetEvent> listenerSupport;
    private final GTListenerSupport<AllEventListener, GTEvent<?>> listenerSupportAll;

    /**
     * Constructor.
     */
    public Net(String id) {
        netDelegate = new ListenableUndirectedGraph<Node, Line>(Line.class);
        lineClasses = new LinkedList<LineClass>();
        listenerSupport = new GTListenerSupport<NetListener, NetEvent>(new GTEventSender<NetListener, NetEvent>() {

            @Override
            public void fireEvent(NetListener listener, NetEvent event) {
                listener.netChanged(event);
            }
        });
        listenerSupportAll = new GTListenerSupport<AllEventListener, GTEvent<?>>(new GTEventSender<AllEventListener, GTEvent<?>>() {
            @Override
            public void fireEvent(AllEventListener listener, GTEvent<?> event) {
                listener.changed(event);
            }
        });
        listener = new GTListenerNetImpl(this);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public Tuple<Node> getNodes(Line track) {
        return new Tuple<Node>(netDelegate.getEdgeSource(track),netDelegate.getEdgeTarget(track));
    }

    public Set<Node> getNodes() {
        return netDelegate.vertexSet();
    }

    public void addNode(Node node) {
        netDelegate.addVertex(node);
        this.fireEvent(new NetEvent(this, GTEventType.NODE_ADDED, node));
        node.addListener(listener);
    }

    public void removeNode(Node node) {
        netDelegate.removeVertex(node);
        this.fireEvent(new NetEvent(this, GTEventType.NODE_REMOVED, node));
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
        this.fireEvent(new NetEvent(this, GTEventType.LINE_ADDED, line));
        line.addListener(listener);
    }

    public void removeLine(Line line) {
        netDelegate.removeEdge(line);
        this.fireEvent(new NetEvent(this, GTEventType.LINE_REMOVED, line));
        line.removeListener(listener);
    }

    public List<Line> getRoute(Node from, Node to) {
        return DijkstraShortestPath.findPathBetween(netDelegate, from, to);
    }

    public List<LineClass> getLineClasses() {
        return Collections.unmodifiableList(lineClasses);
    }

    public void addLineClass(LineClass lineClass) {
        lineClasses.add(lineClass);
        this.fireEvent(new NetEvent(this, GTEventType.LINE_CLASS_ADDED, lineClass));
    }

    public void addLineClass(LineClass lineClass, int position) {
        lineClasses.add(position, lineClass);
        this.fireEvent(new NetEvent(this, GTEventType.LINE_CLASS_ADDED, lineClass));
    }

    public void removeLineClass(LineClass lineClass) {
        lineClasses.remove(lineClass);
        this.fireEvent(new NetEvent(this, GTEventType.LINE_CLASS_REMOVED, lineClass));
    }

    public void moveLineClass(LineClass lineClass, int position) {
        int oldIndex = lineClasses.indexOf(lineClass);
        if (oldIndex == -1)
            throw new IllegalArgumentException("Line class not in list.");
        this.moveLineClass(oldIndex, position);
    }

    public void moveLineClass(int oldIndex, int newIndex) {
        LineClass lineClass = lineClasses.remove(oldIndex);
        lineClasses.add(newIndex, lineClass);
        this.fireEvent(new NetEvent(this, GTEventType.LINE_CLASS_MOVED, lineClass, oldIndex, newIndex));
    }

    public Node getNodeById(String id) {
        for (Node node : netDelegate.vertexSet()) {
            if (node.getId().equals(id))
                return node;
        }
        return null;
    }

    public Line getLineById(String id) {
        for (Line line : netDelegate.edgeSet()) {
            if (line.getId().equals(id))
                return line;
        }
        return null;
    }

    public LineClass getLineClassById(String id) {
        for (LineClass lineClass : getLineClasses()) {
            if (lineClass.getId().equals(id))
                return lineClass;
        }
        return null;
    }

    public Graph<Node, Line> getGraph() {
        return netDelegate;
    }

    public void addListener(NetListener listener) {
        this.listenerSupport.addListener(listener);
    }

    public void removeListener(NetListener listener) {
        this.listenerSupport.removeListener(listener);
    }

    public void addAllEventListener(AllEventListener listener) {
        this.listenerSupportAll.addListener(listener);
    }

    public void removeAllEventListener(AllEventListener listener) {
        this.listenerSupportAll.removeListener(listener);
    }

    void fireNestedEvent(GTEvent<?> event) {
        this.listenerSupportAll.fireEvent(event);
    }

    private void fireEvent(NetEvent event) {
        this.listenerSupport.fireEvent(event);
        this.listenerSupportAll.fireEvent(event);
    }

    @Override
    public String toString() {
        return "Net";
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
        visitor.visitAfter(this);
    }

    public ObjectWithId getObjectById(String id) {
        if (getId().equals(id))
            return this;
        ObjectWithId object = getLineById(id);
        if (object != null)
            return object;
        object = getNodeById(id);
        if (object != null)
            return object;
        object = getLineClassById(id);
        return object;
    }
}
