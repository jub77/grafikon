package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import java.time.Duration;

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

    private static final long serialVersionUID = 1L;

	public interface TrainAction {
        void execute(Train train);
    }

    private final transient ApplicationModel model;

    public RecalculateAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RsActionHandler.getInstance()
            .fromValue(model.get())
            .id("recalculate")
            .component(GuiComponentUtils.getTopLevelComponent(e.getSource()))
            .buildExecution()
            .onEdt()
            .logTime()
            .setMessage(ResourceLoader.getString("wait.message.recalculate"))
            .split(TrainDiagram::getTrains, 10)
            .onEdtWithDelay(Duration.ofMillis(1))
            .addBatchConsumer((context, train) -> train.recalculate())
            .execute();
    }
}
