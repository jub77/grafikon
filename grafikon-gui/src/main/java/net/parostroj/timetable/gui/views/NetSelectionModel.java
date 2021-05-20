package net.parostroj.timetable.gui.views;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    private final Set<NetSelectionListener> listeners;

    private Collection<Object> selectedObjects = Collections.emptyList();

    public interface NetSelectionListener {
        void selection(Collection<Object> item);
    }

    public NetSelectionModel() {
        listeners = new HashSet<>();
    }

    public void addNetSelectionListener(NetSelectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void invoke(Object sender, mxEventObject event) {
    	mxGraphSelectionModel mm = (mxGraphSelectionModel) sender;
    	mxCell cell = (mxCell) mm.getCell();
        if (cell != null && mm.getCells().length > 0) {
            this.callListeners(Arrays.stream(mm.getCells()).map(c -> ((mxCell) c).getValue()).collect(toList()));
        } else {
            this.callListeners(Collections.emptyList());
        }
    }

    private void callListeners(Collection<Object> items) {
        this.selectedObjects = items;
        for (NetSelectionListener listener : listeners) {
            listener.selection(items);
        }
    }

    public Collection<Object> getSelectedObjects() {
        return selectedObjects;
    }

    public void clearSelection() {
        this.callListeners(Collections.emptyList());
    }
}
