package net.parostroj.timetable.gui.actions.impl;

import java.io.File;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.CheckedModelAction;

/**
 * Select correct loader.
 *
 * @author jub
 */
public class SelectLoadAction extends CheckedModelAction {

    public SelectLoadAction(ActionContext context) {
        super(context);
    }

    @Override
    protected void action() {
        // get file
        File file = (File) getActionContext().getAttribute("file");
        String fileName = file.getName();
        if (fileName.endsWith(".gtml")) {
            getActionContext().setAttribute("libraryFile", file);
        } else {
            getActionContext().setAttribute("diagramFile", file);
        }
    }
}
