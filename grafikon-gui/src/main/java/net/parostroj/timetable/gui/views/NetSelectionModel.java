package net.parostroj.timetable.gui.views;

import java.util.HashSet;
import java.util.Set;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraphSelectionModel;

/**
 * Model for net edit view.
 *
 * @author jub
 */
public class NetSelectionModel implements mxIEventListener {

    private Set<NetSelectionListener> listeners;

    private Node selectedNode;
    private Line selectedLine;

    public interface NetSelectionListener {
        public void selection(Object item);
    }

    public NetSelectionModel() {
        listeners = new HashSet<>();
    }

    public void addNetSelectionListener(NetSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeNetSelectionListener(NetSelectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void invoke(Object sender, mxEventObject event) {
    	mxGraphSelectionModel mm = (mxGraphSelectionModel) sender;
    	mxCell cell = (mxCell) mm.getCell();
        selectedLine = null;
        selectedNode = null;
        if (cell != null && mm.getCells().length == 1) {
            if (cell.getValue() instanceof Node) {
                selectedNode = (Node) cell.getValue();
                this.callListeners(selectedNode);
            } else if (cell.getValue() instanceof Line) {
                selectedLine = (Line) cell.getValue();
                this.callListeners(selectedLine);
            }
        } else {
            this.callListeners(null);
        }
    }

    private void callListeners(Object item) {
        for (NetSelectionListener listener : listeners) {
            listener.selection(item);
        }
    }

    public Line getSelectedLine() {
        return selectedLine;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }
}
