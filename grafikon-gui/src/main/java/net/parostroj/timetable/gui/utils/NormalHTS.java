package net.parostroj.timetable.gui.utils;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.components.HighlightedTrains;
import net.parostroj.timetable.gui.components.TrainSelector;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Implementation of HighlightedTrains for normal timetable view.
 *
 * @author jub
 */
public class NormalHTS implements HighlightedTrains, TrainSelector, ApplicationModelListener {

    private Set<Train> set = Collections.emptySet();
    private final Color selectionColor;
    private TimeInterval selectedTimeInterval;
    private final ApplicationModel model;
    private final GraphicalTimetableView view;

    public NormalHTS(final ApplicationModel model, Color selectionColor,
            final GraphicalTimetableView view) {
        model.addListener(new ApplicationModelListener() {

            @Override
            public void modelChanged(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SELECTED_TRAIN_CHANGED) {
                    set = Collections.singleton(model.getSelectedTrain());
                    view.repaint();
                }
            }
        });
        this.selectionColor = selectionColor;
        this.model = model;
        this.view = view;
        this.model.addListener(this);
    }

    @Override
    public boolean isHighlighedInterval(TimeInterval interval) {
        return set.contains(interval.getTrain());
    }

    @Override
    public Color getColor() {
        return this.selectionColor;
    }

    @Override
    public void intervalsSelected(List<TimeInterval> intervals) {
        TimeInterval interval = null;
        if (selectedTimeInterval == null)
            interval = intervals.isEmpty() ? null : intervals.get(0);
        else {
            interval = intervals.isEmpty() ? null : TransformUtil.getNextSelected(intervals, selectedTimeInterval, true);
        }
        // set selected train
        Train selected = null;
        if (interval != null)
            selected = interval.getTrain();
        model.setSelectedTrain(selected);
        selectedTimeInterval = interval;
        model.getMediator().sendMessage(new IntervalSelectionMessage(interval));
    }

    @Override
    public void editSelected() {
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.EDIT_SELECTED_TRAIN, model));
    };

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED)
            view.setTrainDiagram(event.getModel().getDiagram());
    }
}
