package net.parostroj.timetable.gui.actions.impl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.zip.ZipInputStream;

import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.utils.ResourceLoader;

public class LoadLibraryUrlModelAction extends EventDispatchAfterModelAction {

    private static final Logger log = LoggerFactory.getLogger(LoadLibraryUrlModelAction.class);

    private final TrainDiagramType diagramType;

    private String errorMessage;
    private String url;

    public LoadLibraryUrlModelAction(ActionContext context, TrainDiagramType diagramType) {
        super(context);
        this.diagramType = diagramType;
    }

    @Override
    protected void backgroundAction() {
        url = (String) getActionContext().getAttribute("libraryUrl");
        if (url == null) {
            // skip
            return;
        }
        setWaitMessage(ResourceLoader.getString("wait.message.loadlibrary"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            log.debug("Loading library: {}", url);
            try (ZipInputStream is = new ZipInputStream(URI.create(url).toURL().openStream())){
                LSSource source = LSSource.create(is);
                LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(source);
                LSFeature[] features = diagramType == TrainDiagramType.NORMAL
                        ? new LSFeature[0] : new LSFeature[]{LSFeature.RAW_DIAGRAM};
                context.setAttribute("library", ls.load(source, features));
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
            String text = errorMessage + " " + url;
            GuiComponentUtils.showError(text, getActionContext().getLocationComponent());
            context.setCancelled(true);
        }
    }
}
