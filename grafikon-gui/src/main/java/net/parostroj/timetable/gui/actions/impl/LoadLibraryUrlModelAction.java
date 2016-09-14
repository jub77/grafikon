package net.parostroj.timetable.gui.actions.impl;

import java.io.FileNotFoundException;
import java.net.URL;
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

public class LoadLibraryUrlModelAction extends EventDispatchAfterModelAction {

    private static final Logger log = LoggerFactory.getLogger(LoadLibraryUrlModelAction.class);

    private String errorMessage;
    private String url;

    public LoadLibraryUrlModelAction(ActionContext context) {
        super(context);
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
            try (ZipInputStream is = new ZipInputStream(new URL(url).openStream())){
                LSLibrary ls = LSLibraryFactory.getInstance().createForLoad(is);
                context.setAttribute("library", ls.load(is));
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
        }
    }
}
