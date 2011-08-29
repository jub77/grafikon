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
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Delegate for actions over trains cycles.
 * 
 * @author jub
 */
public abstract class TCDelegate implements ApplicationModelListener {

    public enum Action {
        NEW_CYCLE, DELETED_CYCLE, MODIFIED_CYCLE, SELECTED_CHANGED, REFRESH, NEW_TRAIN, DELETED_TRAIN; 
    }
    
    public interface Listener {
        public void tcEvent(Action action, TrainsCycle cycle, Train train);
    }

    private TrainsCycle selected;
    private Set<TCDelegate.Listener> listeners;
    protected ApplicationModel model;
    
    public TCDelegate(ApplicationModel model) {
        this.model = model;
        this.model.addListener(this);
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
    
    public abstract String getType();
    
    public void fireEvent(Action action, TrainsCycle cycle) {
        this.fireEventImpl(action, cycle, null);
    }
    
    public void fireUpdatedTrain(Train train) {
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
    }
    
    private void fireEventImpl(Action action, TrainsCycle cycle, Train train) {
        // propagate action up
        switch (action) {
            case NEW_CYCLE:
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_CYCLE, model, cycle));
                break;
            case DELETED_CYCLE:
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETED_CYCLE, model, cycle));
                break;
            case MODIFIED_CYCLE:
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_CYCLE, model, cycle));
                break;
        }
        // call listeners
        for (Listener listener : listeners) {
            listener.tcEvent(action, cycle, train);
        }
    }
    
    public List<TrainsCycleItem> getTrainCycles(Train train) {
        return train.getCycles(getType());
    }
    
    public boolean showCorrectionWarning() {
        return model.getProgramSettings().isWarningAutoECCorrection();
    }
    
    public abstract String getTrainCycleErrors(TrainsCycle cycle);
    
    public abstract void showEditDialog(JComponent component);
    
    public abstract String getCycleDescription();

    public abstract boolean isOverlappingEnabled();

    public void modelChanged(ApplicationModelEvent event) {
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
                this.fireEvent(Action.REFRESH, null);
                break;
            case NEW_TRAIN:
                this.fireEventImpl(Action.NEW_TRAIN, null, (Train) event.getObject());
                break;
            case DELETE_TRAIN:
                this.fireEventImpl(Action.DELETED_TRAIN, null, (Train) event.getObject());
                break;
        }
    }
    
    public TrainDiagram getTrainDiagram() {
        return model.getDiagram();
    }
}
