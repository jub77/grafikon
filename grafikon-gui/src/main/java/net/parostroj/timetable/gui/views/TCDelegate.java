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
import net.parostroj.timetable.utils.ResourceLoader;

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
            public void processTrainsCycleEvent(Event event) {
                if (event.getSource() == selected) {
                    boolean sequenceChanged = event.getType() == Event.Type.SPECIAL && event.getData() == Special.SEQUENCE;
                    boolean engineClassChanged = event.getType() == Event.Type.ATTRIBUTE
                            && event.getAttributeChange().checkName(TrainsCycle.ATTR_ENGINE_CLASS);
                    boolean itemMoved = event.getType() == Event.Type.MOVED && event.getObject() instanceof TrainsCycleItem;

                    boolean changed = itemMoved || engineClassChanged || sequenceChanged;
                    if (changed) {
                        setSelectedCycle(selected);
                    }
                }
            }
            @Override
            public void processTrainDiagramEvent(Event event) {
                // process add/remove train
                if (event.getType() == Event.Type.ADDED && event.getObject() instanceof Train) {
                    fireEventImpl(Action.NEW_TRAIN, null, (Train) event.getObject());
                } else if (event.getType() == Event.Type.REMOVED && event.getObject() instanceof Train) {
                    fireEventImpl(Action.DELETED_TRAIN, null, (Train) event.getObject());
                } else if (event.getType() == Event.Type.ADDED && event.getObject() instanceof TrainsCycle) {
                    fireEventImpl(Action.NEW_CYCLE, (TrainsCycle) event.getObject(), null);
                } else if (event.getType() == Event.Type.REMOVED && event.getObject() instanceof TrainsCycle) {
                    fireEventImpl(Action.DELETED_CYCLE, (TrainsCycle) event.getObject(), null);
                }
            }
            @Override
            public void processTrainEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE && event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                    fireEventImpl(Action.REFRESH_TRAIN_NAME, null, (Train) event.getSource());
                }
            }
        }, Event.class);
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

    public String getTrainCycleErrors(TrainsCycle cycle) {
        StringBuilder result = new StringBuilder();
        checkConflicts(cycle, result);
        checkStartEnd(cycle, result);
        addListIfSequence(cycle, result);
        return result.toString();
    }

    private void addListIfSequence(TrainsCycle cycle, StringBuilder result) {
        if (cycle.isPartOfSequence()) {
            addNewLineIfNotEmpty(result);
            cycle.applyToSequence(tc -> {
                if (tc != cycle) {
                    result.append(" > ");
                }
                result.append(tc.getName());
            });
        }
    }

    private void checkConflicts(TrainsCycle cycle, StringBuilder result) {
        List<TrainsCycle.Conflict> conflicts = cycle.checkConflicts();
        for (TrainsCycle.Conflict item : conflicts) {
            TrainsCycleItem fromItem = item.getFrom();
            TrainsCycleItem toItem = item.getTo();
            if (fromItem.getToInterval().getOwnerAsNode() != toItem.getFromInterval().getOwnerAsNode()) {
                addNewLineIfNotEmpty(result);
                result.append(String.format(ResourceLoader.getString("ec.problem.nodes"),
                        fromItem.getTrain().getName(),
                        fromItem.getToInterval().getOwnerAsNode().getName(),
                        toItem.getTrain().getName(),
                        toItem.getFromInterval().getOwnerAsNode().getName()));
            } else if (fromItem.getNormalizedEndTime() >= toItem.getNormalizedStartTime()) {
                addNewLineIfNotEmpty(result);
                TimeConverter c = fromItem.getTrain().getDiagram().getTimeConverter();
                result.append(String.format(ResourceLoader.getString("ec.problem.time"),
                        fromItem.getTrain().getName(),
                        c.convertIntToText(fromItem.getEndTime()),
                        toItem.getTrain().getName(),
                        c.convertIntToText(toItem.getStartTime())));
            }
        }
    }

    private void checkStartEnd(TrainsCycle cycle, StringBuilder result) {
        if (!cycle.isEmpty()) {
            boolean startMatch = cycle.getFirstItem().getFromNode() == cycle.getPrevious().getLastItem().getToNode();
            boolean endMatch = cycle.getLastItem().getToNode() == cycle.getNext().getFirstItem().getFromNode();
            if (!startMatch || !endMatch) {
                addNewLineIfNotEmpty(result);
                result.append(ResourceLoader.getString("ec.problem.startend"));
            }
        }
    }

    protected void addNewLineIfNotEmpty(StringBuilder result) {
        if (result.length() != 0) {
            result.append('\n');
        }
    }

    public abstract void showEditDialog(JComponent component);

    public String getCycleDescription() {
        return getSelectedCycle().getDisplayDescription();
    }

    public boolean isOverlappingEnabled() {
        return true;
    }

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
