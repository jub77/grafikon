package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.actions.impl.CloseableFileChooser;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.components.ExportImportSelectionSource;
import net.parostroj.timetable.gui.dialogs.ExportImportSelectionBaseDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryBuilder;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Export action.
 *
 * @author jub
 */
public class ExportAction extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(ExportAction.class);

    private final ExportImportSelectionBaseDialog exportDialog;
    private final ApplicationModel model;

    public ExportAction(ApplicationModel model, Frame frame) {
        this.model = model;
        this.exportDialog = new ExportImportSelectionBaseDialog(frame, true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Component parent = GuiComponentUtils.getTopLevelComponent(event.getSource());

        ExportImportSelectionSource source = ExportImportSelectionSource.fromDiagramToLibrary(model.getDiagram());

        exportDialog.setSelectionSource(source);
        exportDialog.setLocationRelativeTo(parent);
        exportDialog.setVisible(true);
        boolean cancelled = exportDialog.isCancelled();

        if (!cancelled) {
            // saving train diagram
            try (CloseableFileChooser gtmlFileChooser = FileChooserFactory.getInstance()
                    .getFileChooser(FileChooserFactory.Type.GTML)) {
                int retVal = gtmlFileChooser.showSaveDialog(parent);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    model.setOpenedFile(gtmlFileChooser.getSelectedFile());
                    ActionContext c = new ActionContext(parent);
                    ModelAction action = getSaveModelAction(c, gtmlFileChooser.getSelectedFile(), parent, this.createLibrary(exportDialog.getSelection()));
                    ActionHandler.getInstance().execute(action);
                }
            }
        }
    }

    private Library createLibrary(Map<ImportComponent, Collection<ObjectWithId>> map) {
        LibraryBuilder libBuilder = new LibraryBuilder(new LibraryBuilder.Config().setAddMissing(true));
        map.values().stream().flatMap(item -> item.stream()).forEach(object -> libBuilder.importObject(object));
        return libBuilder.build();
    }

    public static ModelAction getSaveModelAction(ActionContext context, final File file, final Component parent, final Library library) {
        ModelAction action = new EventDispatchAfterModelAction(context) {

            private String errorMessage;

            @Override
            protected void backgroundAction() {
                setWaitMessage(ResourceLoader.getString("wait.message.savelibrary"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    ModelUtils.saveLibraryData(library, file);
                } catch (LSException e) {
                    log.warn("Error saving library.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } catch (Exception e) {
                    log.warn("Error saving library.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } finally {
                    log.debug("Saved in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (errorMessage != null) {
                    GuiComponentUtils.showError(errorMessage + " " + file.getName(), parent);
                }
            }
        };

        return action;
    }
}
