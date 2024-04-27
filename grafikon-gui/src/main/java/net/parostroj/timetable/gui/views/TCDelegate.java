/*
 * TCDelegate.java
 *
 * Created on 16.9.2007, 14:31:31
 */
package net.parostroj.timetable.gui.views;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JComponent;

import net.parostroj.timetable.actions.TrainsCycleChecker;
import net.parostroj.timetable.actions.TrainsCycleChecker.Conflict;
import net.parostroj.timetable.actions.TrainsCycleChecker.ConflictType;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Special;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Delegate for actions with train circulations.
 *
 * @author jub
 */
public abstract class TCDelegate implements ApplicationModelListener {

    public enum Action {
        NEW_CYCLE, DELETED_CYCLE, MODIFIED_CYCLE, SELECTED_CHANGED, REFRESH, NEW_TRAIN, DELETED_TRAIN, DIAGRAM_CHANGE, REFRESH_TRAIN_NAME
    }

    public interface Listener {
        void tcEvent(Action action, TrainsCycle cycle, Train train);
    }

    private TrainsCycle selected;
    private final Set<TCDelegate.Listener> listeners;
    protected ApplicationModel model;
    protected TrainsCycleChecker checker;

    protected TCDelegate(ApplicationModel model) {
        this(model, TrainsCycleChecker.forVehicleType());
    }

    protected TCDelegate(ApplicationModel model, TrainsCycleChecker checker) {
        this.checker = checker;
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
        this.listeners = new HashSet<>();
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

    public String getTrainCycleErrors(TrainsCycle cycle) {
        StringBuilder result = new StringBuilder();
        checkConflicts(cycle, result);
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
        List<Conflict> conflicts = checker.checkConflicts(cycle);
        TimeConverter c = cycle.getDiagram().getTimeConverter();
        for (Conflict item : conflicts) {
            TrainsCycleItem fromItem = item.getFrom();
            TrainsCycleItem toItem = item.getTo();
            for (ConflictType conflictType : item.getType()) {
                switch (conflictType) {
                case NODE:
                    addNewLineIfNotEmpty(result);
                    result.append(String.format("%s %s", ResourceLoader.getString("ec.problem.nodes"),
                            formatItems(fromItem, toItem, c)));
                    if (!cycle.isPartOfSequence() && fromItem == cycle.getLastItem()
                            && toItem == cycle.getFirstItem()) {
                        addNewLineIfNotEmpty(result);
                        result.append(ResourceLoader.getString("ec.problem.startend"));
                    }
                    break;
                case TIME:
                    addNewLineIfNotEmpty(result);
                    result.append(String.format("%s %s", ResourceLoader.getString("ec.problem.time"),
                            formatItems(fromItem, toItem, c)));
                    break;
                default:
                    break;
                }
            }
        }
    }

    protected String formatItems(TrainsCycleItem from, TrainsCycleItem to, TimeConverter c) {
        String text = String.format("%s (%s[%s]), %s (%s[%s])",
                from.getTrain().getDefaultName(),
                from.getToInterval().getOwnerAsNode().getName(),
                c.convertIntToText(from.getEndTime()),
                to.getTrain().getDefaultName(),
                to.getFromInterval().getOwnerAsNode().getName(),
                c.convertIntToText(to.getStartTime())
            );
        if (from.getCycle() != to.getCycle()) {
            // add information about circulation
            text = String.format("%s, %s -> %s", text, from.getCycle().getName(), to.getCycle().getName());
        }
        return text;
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
        if (Objects.requireNonNull(event.getType()) == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
            this.fireEvent(Action.DIAGRAM_CHANGE, null);
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
