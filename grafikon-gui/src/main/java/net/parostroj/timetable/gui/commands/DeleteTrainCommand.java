package net.parostroj.timetable.gui.commands;

import java.util.ArrayList;
import java.util.List;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;

/**
 * Delete train command.
 *
 * @author jub
 */
public class DeleteTrainCommand implements Command {

    private final Train train;

    public DeleteTrainCommand(Train train) {
        this.train = train;
    }

    @Override
    public void accept(ApplicationModel model) {
        TrainDiagram diagram = model.getDiagram();
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
