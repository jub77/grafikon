package net.parostroj.timetable.gui.utils;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.components.TrainSelector;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Implementation of TrainSelector for normal timetable view.
 *
 * @author jub
 */
public class NormalTrainSelector implements TrainSelector {

    private TimeInterval selectedTimeInterval;
    private final ApplicationModel model;

    public NormalTrainSelector(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void selectTrainInterval(TimeInterval interval) {
        // set selected train
        Train selected = null;
        if (interval != null)
            selected = interval.getTrain();
        model.setSelectedTrain(selected);
        selectedTimeInterval = interval;
    }

    @Override
    public TimeInterval getSelectedTrainInterval() {
        return selectedTimeInterval;
    }

}
