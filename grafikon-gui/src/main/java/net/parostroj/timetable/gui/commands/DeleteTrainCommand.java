package net.parostroj.timetable.gui.commands;

import java.util.ArrayList;
import java.util.List;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;

/**
 * Delete train command.
 *
 * @author jub
 */
public class DeleteTrainCommand {

    private final Train train;

    public DeleteTrainCommand(Train train) {
        this.train = train;
    }

    public void execute(TrainDiagram diagram) {
        // remove train from cycles
        for (TrainsCycleType type : diagram.getCycleTypes()) {
            if (!train.getCycles(type).isEmpty()) {
                this.removeTrainFromCycles(train.getCycles(type));
            }
        }
        // remove from list of trains
        diagram.getTrains().remove(train);
    }

    private void removeTrainFromCycles(List<TrainsCycleItem> items) {
        for (TrainsCycleItem item : new ArrayList<>(items)) {
            item.getCycle().removeItem(item);
        }
    }
}
