package net.parostroj.timetable.gui.utils;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.gui.components.GraphicalTimetableView;
import net.parostroj.timetable.gui.components.HighlightedTrains;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Implementation of HighlightedTrains for normal timetable view.
 *
 * @author jub
 */
public class NormalHighlightedTrains implements HighlightedTrains {

    private Set<Train> set = Collections.emptySet();
    private final Color selectionColor;

    public NormalHighlightedTrains(final ApplicationModel model, Color selectionColor,
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
    }

    @Override
    public boolean isHighlighedInterval(TimeInterval interval) {
        return set.contains(interval.getTrain());
    }

    @Override
    public Color getColor() {
        return this.selectionColor;
    }

}
