package net.parostroj.timetable.gui.actions.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;

import net.parostroj.timetable.model.TrainDiagramType;
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

    private final TrainDiagramType diagramType;

    private String errorMessage;
    private File selectedFile;

    public LoadLibraryModelAction(ActionContext context, TrainDiagramType diagramType) {
        super(context);
        this.diagramType = diagramType;
    }

    @Override
    protected void backgroundAction() {
        selectedFile = (File) getActionContext().getAttribute("libraryFile");
        if (selectedFile == null) {
            // skip
            return;
        }
        setWaitMessage(ResourceLoader.getString("wait.message.loadlibrary"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            try {
                LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(selectedFile);
                try (ZipInputStream stream = new ZipInputStream(new FileInputStream(selectedFile))) {
                    context.setAttribute("library", ls.load(diagramType, stream));
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
            GuiComponentUtils.showError(text, getActionContext().getLocationComponent());
            context.setCancelled(true);
        }
    }
}
