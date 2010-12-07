package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.parostroj.timetable.gui.ApplicationModel;

/**
 * This action executes a script.
 *
 * @author jub
 */
public class ExecuteScriptAction extends AbstractAction {

    private final ApplicationModel model;

    public ExecuteScriptAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        model.getDiagram();
    }
}
