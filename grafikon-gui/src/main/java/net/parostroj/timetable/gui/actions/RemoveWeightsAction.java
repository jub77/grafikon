package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.RecalculateAction.TrainAction;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Removes weight attribute from all trains.
 * 
 * @author jub
 */
public class RemoveWeightsAction extends AbstractAction {
    
    private ApplicationModel model;
    
    public RemoveWeightsAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Component parent = ActionUtils.getTopLevelComponent(e.getSource());
        int result = JOptionPane.showConfirmDialog(parent, ResourceLoader.getString("dialog.confirm.action.progress"), ResourceLoader.getString("menu.special.remove.weights"), JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // remove weights
            ActionContext context = new ActionContext(parent);
            ModelAction action = RecalculateAction.getAllTrainsAction(context, model.getDiagram(), new TrainAction() {
                
                @Override
                public void execute(Train train) throws Exception {
                    train.removeAttribute("weight");
                }
            }, ResourceLoader.getString("wait.message.recalculate"), "Weight removal");
            ActionHandler.getInstance().execute(action);
        }
    }
}
