package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * This action recalculates all trains.
 *
 * @author jub
 */
public class RecalculateAction extends AbstractAction {

    public static interface TrainAction {
        public void execute(Train train) throws Exception;
    }

    private final ApplicationModel model;

    public RecalculateAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RxActionHandler.getInstance()
            .newExecution("recalculate", GuiComponentUtils.getTopLevelComponent(e.getSource()), model.get())
            .onBackground()
            .logTime()
            .setMessage(ResourceLoader.getString("wait.message.recalculate"))
            .split(TrainDiagram::getTrains, 10)
            .addEdtBatchConsumer((context, train) -> {
                train.recalculate();
            })
            .execute();
    }
}
