package net.parostroj.timetable.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.actions.ActionUtils;
import net.parostroj.timetable.gui.actions.ModelUtils;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.ModelAction;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Window listener for the MainFrame.
 *
 * @author jub
 */
public class MainFrameWindowListener extends WindowAdapter {

    private static final Logger LOG = Logger.getLogger(MainFrameWindowListener.class.getName());
    private MainFrame parent;
    private ApplicationModel model;

    public MainFrameWindowListener(ApplicationModel model, MainFrame parent) {
        this.parent = parent;
        this.model = model;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result != JOptionPane.CANCEL_OPTION) {
            ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.programclose"), 0, new ModelAction() {

                private String errorMessage;

                @Override
                public void run() {
                    try {
                        if (result == JOptionPane.YES_OPTION) {
                            ModelUtils.saveModelData(model, model.getOpenedFile());
                        }
                        parent.cleanUpBeforeApplicationEnd();
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Error saving model.", e);
                        errorMessage = ResourceLoader.getString("dialog.error.saving");
                    }
                }

                @Override
                public void afterRun() {
                    if (errorMessage != null) {
                        ActionUtils.showError(errorMessage, parent);
                        return;
                    }
                    parent.dispose();
                }
            });
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        System.exit(0);
    }
}
