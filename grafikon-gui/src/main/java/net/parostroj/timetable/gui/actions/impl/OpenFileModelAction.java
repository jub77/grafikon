package net.parostroj.timetable.gui.actions.impl;

import java.io.File;

import javax.swing.JFileChooser;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.EventDispatchModelAction;

/**
 * Open file model action.
 *
 * <ul>
 *      <li>fileType: FileChooserFactory.Type</li>
 *      <li>file: selected file</li>
 * </ul>
 *
 * @author jub
 */
public class OpenFileModelAction extends EventDispatchModelAction {

    public OpenFileModelAction(ActionContext context) {
        super(context);
    }

    @Override
    protected void eventDispatchAction() {
        FileChooserFactory.Type fileType = (FileChooserFactory.Type) context.getAttribute("fileType");
        try (CloseableFileChooser gtmFileChooser = FileChooserFactory.getInstance().getFileChooser(fileType)) {
            final int retVal = gtmFileChooser.showOpenDialog(getActionContext().getLocationComponent());
            if (retVal == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = gtmFileChooser.getSelectedFile();
                context.setAttribute("file", selectedFile);
            } else {
                // cancel
                context.setCancelled(true);
            }
        }
    }
}
