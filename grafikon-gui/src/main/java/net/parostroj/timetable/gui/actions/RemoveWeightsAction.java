package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.time.Duration;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.RsActionHandler;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Removes weight attribute from all trains.
 *
 * @author jub
 */
public class RemoveWeightsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

	private final transient ApplicationModel model;

    public RemoveWeightsAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Component parent = GuiComponentUtils.getTopLevelComponent(e.getSource());
        int result = JOptionPane.showConfirmDialog(parent,
                ResourceLoader.getString("dialog.confirm.action.progress"),
                ResourceLoader.getString("menu.special.remove.weights"),
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // remove weights
            RsActionHandler.getInstance()
                .fromValue(model.get())
                .id("weight_removal")
                .component(GuiComponentUtils.getTopLevelComponent(e.getSource()))
                .buildExecution()
                    .onEdt()
                    .logTime()
                    .setMessage(ResourceLoader.getString("wait.message.recalculate"))
                    .split(TrainDiagram::getTrains, 10)
                    .onEdtWithDelay(Duration.ofMillis(1))
                    .addBatchConsumer((context, train) -> train.removeAttribute(Train.ATTR_WEIGHT))
                    .execute();
        }
    }
}
