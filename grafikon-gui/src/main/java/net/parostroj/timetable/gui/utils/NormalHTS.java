package net.parostroj.timetable.gui.utils;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.events.DiagramChangeMessage;
import net.parostroj.timetable.gui.events.EditTrainMessage;
import net.parostroj.timetable.gui.events.IntervalSelectionMessage;
import net.parostroj.timetable.gui.events.TrainSelectionMessage;
import net.parostroj.timetable.mediator.Mediator;
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
    private Train selectedTrain;
    private final Mediator mediator;
    private final GraphicalTimetableView view;
    private boolean selection;
    private boolean isEvent;

    public NormalHTS(Mediator mediator, Color selectionColor,
            final GraphicalTimetableView view) {
        this.selectionColor = selectionColor;
        this.view = view;
        this.mediator = mediator;
        this.mediator.addColleague(this::trainSelectionChanged, TrainSelectionMessage.class);
        this.mediator.addColleague(this::diagramChanged, DiagramChangeMessage.class);
    }

    private void trainSelectionChanged(Object object) {
        isEvent = true;
        try {
            if (!selection) {
                selectedTrain = ((TrainSelectionMessage) object).train();
                view.selectItems(selectedTrain == null ? List.of()
                        : selectedTrain.getTimeIntervalList(), TimeInterval.class);
            }
        } finally {
            isEvent = false;
        }
    }

    private void diagramChanged(Object message) {
        view.setTrainDiagram(((DiagramChangeMessage) message).diagram());
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
            Train train = interval != null ? interval.getTrain() : null;
            boolean trainChange = train != selectedTrain;
            selectedTrain = train;
            set = selectedTrain == null ? Set.of() : Set.of(selectedTrain);
            boolean intervalChange = selectedTimeInterval != interval;
            selectedTimeInterval = interval;
            if (!isEvent) {
                if (trainChange) {
                    mediator.sendMessage(new TrainSelectionMessage(selectedTrain));
                }
                if (intervalChange) {
                    mediator.sendMessage(new IntervalSelectionMessage(interval));
                }
            }
            return interval != null;
        } finally {
            selection = false;
        }
    }

    @Override
    public List<TimeInterval> getSelected() {
        return selectedTrain == null ? List.of() : selectedTrain.getTimeIntervalList();
    }

    @Override
    public boolean editSelected() {
        mediator.sendMessage(new EditTrainMessage(selectedTrain));
        return true;
    }
}
