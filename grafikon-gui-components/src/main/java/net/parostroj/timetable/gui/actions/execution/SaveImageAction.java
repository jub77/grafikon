package net.parostroj.timetable.gui.actions.execution;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.output2.OutputException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action for saving image.
 *
 * @author jub
 */
public class SaveImageAction extends EventDispatchAfterModelAction {

    public interface DrawOutput {
        void draw(Dimension size, File output, SaveImageDialog.Type type) throws OutputException;
    }

    private static final Logger log = LoggerFactory.getLogger(SaveImageAction.class);
    private boolean error;
    private final SaveImageDialog dialog;
    private final DrawOutput image;

    public SaveImageAction(ActionContext context, SaveImageDialog dialog, DrawOutput image) {
        super(context);
        this.dialog = dialog;
        this.image = image;
    }

    @Override
    protected void backgroundAction() {
        setWaitMessage(ResourceLoader.getString("wait.message.image.save"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            try {
                image.draw(dialog.getSaveSize(), dialog.getSaveFile(), dialog.getImageType());
            } catch (OutputException e) {
                log.warn("Error saving file: " + dialog.getSaveFile(), e);
                error = true;
            }
        } finally {
            log.debug("Image save finished in {}ms", System.currentTimeMillis() - time);
            setWaitDialogVisible(false);
        }
    }

    @Override
    protected void eventDispatchActionAfter() {
        if (error) {
            JOptionPane.showMessageDialog(context.getLocationComponent(), ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
