package net.parostroj.timetable.gui.utils;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.events.IntervalSelectionMessage;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.output2.gt.*;

/**
 * Implementation of HighlightedTrains for normal timetable view.
 *
 * @author jub
 */
public class NormalHTS implements HighlightedTrains, RegionSelector<TimeInterval> {

    private Set<Train> set = Set.of();
    private final Color selectionColor;
    private TimeInterval selectedTimeInterval;
    private final ApplicationModel model;
    private final GraphicalTimetableView view;
    private boolean selection;
    private boolean appEvent;

    public NormalHTS(final ApplicationModel model, Color selectionColor,
            final GraphicalTimetableView view) {
        this.selectionColor = selectionColor;
        this.model = model;
        this.view = view;
        model.addListener(this::selectedTrainChanged);
        this.model.addListener(this::diagramChanged);
    }

    private void selectedTrainChanged(ApplicationModelEvent event) {
        appEvent = true;
        try {
            if (!selection && event.getType() == ApplicationModelEventType.SELECTED_TRAIN_CHANGED) {
                final Train selectedTrain = model.getSelectedTrain();
                view.selectItems(selectedTrain == null ? List.of()
                        : selectedTrain.getTimeIntervalList(), TimeInterval.class);
            }
        } finally {
            appEvent = false;
        }
    }

    private void diagramChanged(ApplicationModelEvent event) {
        if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED)
            view.setTrainDiagram(event.getModel().getDiagram());
    }

    @Override
    public boolean isHighlighedInterval(TimeInterval interval) {
        return set.contains(interval.getTrain());
    }

    @Override
    public Color getColor(TimeInterval interval) {
        return this.selectionColor;
    }

    @Override
    public boolean regionsSelected(List<TimeInterval> intervals) {
        selection = true;
        try {
            TimeInterval interval = SelectorUtils.select(intervals, selectedTimeInterval, SelectorUtils.createUniqueTrainIntervalFilter());
            // set selected train
            Train selected = interval != null ? interval.getTrain() : null;
            set = selected == null ? Set.of() : Set.of(selected);
            model.setSelectedTrain(selected);
            selectedTimeInterval = interval;
            if (!appEvent) {
                model.getMediator().sendMessage(new IntervalSelectionMessage(interval));
            }
            return interval != null;
        } finally {
            selection = false;
        }
    }

    @Override
    public List<TimeInterval> getSelected() {
        Train train = model.getSelectedTrain();
        return train == null ? List.of() : train.getTimeIntervalList();
    }

    @Override
    public boolean editSelected() {
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.EDIT_SELECTED_TRAIN, model));
        return true;
    }
}
