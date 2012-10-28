package net.parostroj.timetable.gui.commands;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.model.*;

/**
 * Delete train command.
 *
 * @author jub
 */
public class DeleteTrainCommand extends Command {

    private final Train train;

    public DeleteTrainCommand(Train train) {
        this.train = train;
    }

    @Override
    public void execute(ApplicationModel model) throws CommandException {
        TrainDiagram diagram = model.getDiagram();
        // remove train from cycles
        for (String type : diagram.getCycleTypeNames()) {
            if (!train.getCycles(type).isEmpty()) {
                this.removeTrainFromCycles(train.getCycles(type), model);
            }
        }

        diagram.removeTrain(train); // remove from list of trains
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETE_TRAIN, model, train));
    }

    private void removeTrainFromCycles(List<TrainsCycleItem> items, ApplicationModel model) {
        for (TrainsCycleItem item : new LinkedList<TrainsCycleItem>(items)) {
            TrainsCycle cycle = item.getCycle();
            item.getCycle().removeItem(item);
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETED_CYCLE, model, cycle));
        }
    }

    @Override
    public void undo(ApplicationModel model) throws CommandException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
