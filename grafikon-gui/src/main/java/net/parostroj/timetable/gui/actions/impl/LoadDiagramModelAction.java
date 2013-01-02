package net.parostroj.timetable.gui.actions.impl;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;

public class LoadDiagramModelAction extends EventDispatchAfterModelAction {

    private static final Logger LOG = LoggerFactory.getLogger(LoadDiagramModelAction.class);

    private final File selectedFile;
    private final Component parent;
    private final JFileChooser xmlFileChooser;
    private String errorMessage;

    public LoadDiagramModelAction(ActionContext context, File selectedFile, Component parent,
            JFileChooser xmlFileChooser) {
        super(context);
        this.selectedFile = selectedFile;
        this.parent = parent;
        this.xmlFileChooser = xmlFileChooser;
    }

    @Override
    protected void backgroundAction() {
        setWaitMessage(ResourceLoader.getString("wait.message.loadmodel"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            try {
                FileLoadSave ls = LSFileFactory.getInstance().createForLoad(selectedFile);
                context.setAttribute("diagram", ls.load(selectedFile));
            } catch (LSException e) {
                LOG.warn("Error loading model.", e);
                if (e.getCause() instanceof FileNotFoundException)
                    errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
                else
                    errorMessage = ResourceLoader.getString("dialog.error.loading");
            } catch (Exception e) {
                LOG.warn("Error loading model.", e);
                errorMessage = ResourceLoader.getString("dialog.error.loading");
            }
        } finally {
            LOG.debug("Library loaded in {}ms", System.currentTimeMillis() - time);
            setWaitDialogVisible(false);
        }
    }

    @Override
    protected void eventDispatchActionAfter() {
        if (errorMessage != null) {
            String text = errorMessage + " " + xmlFileChooser.getSelectedFile().getName();
            ActionUtils.showError(text, parent);
        }
    }
}
