package net.parostroj.timetable.gui.actions.impl;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSLibrary;
import net.parostroj.timetable.model.ls.LSLibraryFactory;
import net.parostroj.timetable.utils.ResourceLoader;

public class LoadLibraryModelAction extends EventDispatchAfterModelAction {

    private static final Logger log = LoggerFactory.getLogger(LoadLibraryModelAction.class);

    private final File selectedFile;
    private final Component parent;
    private String errorMessage;

    public LoadLibraryModelAction(ActionContext context, File selectedFile, Component parent) {
        super(context);
        this.selectedFile = selectedFile;
        this.parent = parent;
    }

    @Override
    protected void backgroundAction() {
        setWaitMessage(ResourceLoader.getString("wait.message.loadlibrary"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            try {
                LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(selectedFile);
                try (ZipInputStream stream = new ZipInputStream(new FileInputStream(selectedFile))) {
                    context.setAttribute("library", ls.load(stream));
                }
            } catch (LSException e) {
                log.warn("Error loading model.", e);
                if (e.getCause() instanceof FileNotFoundException) {
                    errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
                } else {
                    errorMessage = ResourceLoader.getString("dialog.error.loading");
                }
            } catch (Exception e) {
                log.warn("Error loading model.", e);
                errorMessage = ResourceLoader.getString("dialog.error.loading");
            }
        } finally {
            log.debug("Library loaded in {}ms", System.currentTimeMillis() - time);
            setWaitDialogVisible(false);
        }
    }

    @Override
    protected void eventDispatchActionAfter() {
        if (errorMessage != null) {
            String text = errorMessage + " " + selectedFile;
            GuiComponentUtils.showError(text, parent);
        }
    }
}
