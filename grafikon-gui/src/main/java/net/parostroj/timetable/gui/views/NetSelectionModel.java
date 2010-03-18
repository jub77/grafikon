package net.parostroj.timetable.gui.views;

import java.util.HashSet;
import java.util.Set;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;

/**
 * Model for net edit view.
 *
 * @author jub
 */
public class NetSelectionModel implements GraphSelectionListener {

    private Set<NetSelectionListener> listeners;

    private Node selectedNode;
    private Line selectedLine;

    public static enum Action {
        NODE_SELECTED, LINE_SELECTED, NOTHING_SELECTED;
    }

    public interface NetSelectionListener {
        public void selection(Action action, Node node, Line line);
    }

    public NetSelectionModel() {
        listeners = new HashSet<NetSelectionListener>();
    }

    public void addNetSelectionListener(NetSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeNetSelectionListener(NetSelectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void valueChanged(GraphSelectionEvent event) {
        selectedLine = null;
        selectedNode = null;
        if (event.isAddedCell()) {
            DefaultGraphCell gc = (DefaultGraphCell)event.getCell();
            if (gc.getUserObject() instanceof Node) {
                selectedNode = (Node)gc.getUserObject();
                this.callListeners(Action.NODE_SELECTED, selectedNode, null);
            } else if (gc.getUserObject() instanceof Line) {
                selectedLine = (Line)gc.getUserObject();
                this.callListeners(Action.LINE_SELECTED, null, selectedLine);
            }
        } else {
            this.callListeners(Action.NOTHING_SELECTED, null, null);
        }
    }

    private void callListeners(Action action, Node node, Line line) {
        for (NetSelectionListener listener : listeners) {
            listener.selection(action, node, line);
        }
    }

    public Line getSelectedLine() {
        return selectedLine;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }
}
