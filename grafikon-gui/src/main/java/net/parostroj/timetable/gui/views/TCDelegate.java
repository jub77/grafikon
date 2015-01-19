/*
 * TCDelegate.java
 *
 * Created on 16.9.2007, 14:31:31
 */
package net.parostroj.timetable.gui.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;

/**
 * Delegate for actions over trains cycles.
 *
 * @author jub
 */
public abstract class TCDelegate implements ApplicationModelListener {

    public enum Action {
        NEW_CYCLE, DELETED_CYCLE, MODIFIED_CYCLE, SELECTED_CHANGED, REFRESH, NEW_TRAIN, DELETED_TRAIN, DIAGRAM_CHANGE, REFRESH_TRAIN_NAME;
    }

    public interface Listener {
        public void tcEvent(Action action, TrainsCycle cycle, Train train);
    }

    private TrainsCycle selected;
    private final Set<TCDelegate.Listener> listeners;
    protected ApplicationModel model;

    public TCDelegate(ApplicationModel model) {
        this.model = model;
        this.model.addListener(this);
        this.model.getMediator().addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainsCycleEvent(TrainsCycleEvent event) {
                // if selected and type == item moved
                if (event.getSource() == selected && (event.getType() == GTEventType.CYCLE_ITEM_MOVED
                        || event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(TrainsCycle.ATTR_ENGINE_CLASS))) {
                    // deselect
                    setSelectedCycle(selected);
                }
            }
            @Override
            public void processTrainDiagramEvent(TrainDiagramEvent event) {
                // process add/remove train
                if (event.getType() == GTEventType.TRAIN_ADDED) {
                    fireEventImpl(Action.NEW_TRAIN, null, (Train) event.getObject());
                } else if (event.getType() == GTEventType.TRAIN_REMOVED) {
                    fireEventImpl(Action.DELETED_TRAIN, null, (Train) event.getObject());
                } else if (event.getType() == GTEventType.TRAINS_CYCLE_ADDED) {
                    fireEventImpl(Action.NEW_CYCLE, (TrainsCycle) event.getObject(), null);
                } else if (event.getType() == GTEventType.TRAINS_CYCLE_REMOVED) {
                    fireEventImpl(Action.DELETED_CYCLE, (TrainsCycle) event.getObject(), null);
                }
            }
            @Override
            public void processTrainEvent(TrainEvent event) {
                if (event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                    fireEventImpl(Action.REFRESH_TRAIN_NAME, null, event.getSource());
                }
            }
        }, GTEvent.class);
        this.listeners = new HashSet<Listener>();
    }

    public void setSelectedCycle(TrainsCycle cycle) {
        this.selected = cycle;
        this.fireEvent(Action.SELECTED_CHANGED, cycle);
    }

    public TrainsCycle getSelectedCycle() {
        return selected;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public abstract TrainsCycleType getType();

    public void fireEvent(Action action, TrainsCycle cycle) {
        this.fireEventImpl(action, cycle, null);
    }

    private void fireEventImpl(Action action, TrainsCycle cycle, Train train) {
        // handle event
        this.handleEvent(action, cycle, train);
        // call listeners
        for (Listener listener : listeners) {
            listener.tcEvent(action, cycle, train);
        }
    }

    public List<TrainsCycleItem> getTrainCycles(Train train) {
        return train.getCycles(getType());
    }

    public abstract String getTrainCycleErrors(TrainsCycle cycle);

    public abstract void showEditDialog(JComponent component);

    public abstract String getCycleDescription();

    public abstract boolean isOverlappingEnabled();

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
                this.fireEvent(Action.DIAGRAM_CHANGE, null);
                break;
            default:
                break;
        }
    }

    public TrainDiagram getTrainDiagram() {
        return model.getDiagram();
    }

    public void handleEvent(Action action, TrainsCycle cycle, Train train) {
        // default behaviour -> DIAGRAM_CHANGE initiates REFRESH
        if (action == Action.DIAGRAM_CHANGE)
            this.fireEvent(Action.REFRESH, null);
    }
}
