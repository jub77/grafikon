package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;

import java.time.Duration;
import java.time.Instant;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.MainFrame;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.utils.ResourceLoader;

import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exit action.
 *
 * @author jub
 */
public class ExitAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ExitAction.class);
    private final transient ApplicationModel model;
    private final MainFrame parent;

    public ExitAction(ApplicationModel model, MainFrame parent) {
        this.model = model;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // exiting application
        ModelAction action = getExitAction(parent, model, true);
        ActionHandler.getInstance().execute(action);
    }

    public static ModelAction getExitAction(final MainFrame parent, final ApplicationModel model, final boolean exit) {
        return new CombinedModelAction(new ActionContext()) {

            private int result = JOptionPane.NO_OPTION;
            private String errorMessage;
            private long time;

            @Override
            protected void eventDispatchActionBefore() {
                context.setDelay(0);
                context.setLocationComponent(parent);
                result = ModelUtils.checkModelChangedContinue(model, parent);
                time = System.currentTimeMillis();
            }

            @Override
            protected void backgroundAction() {
                if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                setWaitMessage("");
                setWaitDialogVisible(true);
                try {
                    if (result == JOptionPane.YES_OPTION) {
                        setWaitMessage(ResourceLoader.getString("wait.message.savemodel"));
                        long saveTime = System.currentTimeMillis();
                        ModelUtils.saveModelData(model, model.getOpenedFile());
                        log.debug("Save before exit finished in {}ms", System.currentTimeMillis() - saveTime);
                    }
                    setWaitMessage(ResourceLoader.getString("wait.message.programclose"));
                    parent.cleanUpBeforeApplicationEnd();
                } catch (Exception e) {
                    log.warn("Error saving model.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } finally {
                    setWaitDialogVisible(false);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                if (errorMessage != null) {
                    GuiComponentUtils.showError(errorMessage, parent);
                    return;
                }
                // dispose main window - it should close application
                parent.dispose();

                log.debug("Running time: {}", PeriodFormat.getDefault().print(
                        org.joda.time.Duration.standardSeconds(
                                Duration.between(model.getStartTime(),
                                        Instant.now()).toSeconds()).toPeriod(PeriodType.time())));
                log.debug("Exit finished in {}ms", System.currentTimeMillis() - time);

                // close application by force (possible problems with web start)
                if (exit) {
                    System.exit(0);
                }
            }
        };
    }
}
