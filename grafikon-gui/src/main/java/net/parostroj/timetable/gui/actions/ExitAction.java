package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.MainFrame;
import net.parostroj.timetable.gui.utils.AbstractModelAction;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exit action.
 *
 * @author jub
 */
public class ExitAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExitAction.class.getName());
    private ApplicationModel model;
    private MainFrame parent;

    public ExitAction(ApplicationModel model, MainFrame parent) {
        this.model = model;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // exiting application
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result != JOptionPane.CANCEL_OPTION) {
            ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.programclose"), 0, new AbstractModelAction() {

                private String errorMessage;

                @Override
                public void run() {
                    try {
                        if (result == JOptionPane.YES_OPTION) {
                            ModelUtils.saveModelData(model, model.getOpenedFile());
                        }
                        parent.cleanUpBeforeApplicationEnd();
                    } catch (Exception e) {
                        LOG.warn("Error saving model.", e);
                        errorMessage = ResourceLoader.getString("dialog.error.saving");
                    }
                }

                @Override
                public void afterRun() {
                    if (errorMessage != null) {
                        ActionUtils.showError(errorMessage, parent);
                        return;
                    }
                    // dispose main window - it should close application
                    parent.dispose();
                    // close application by force (possible problems with web start)
                    System.exit(0);
                }
            });
        }
    }
}
