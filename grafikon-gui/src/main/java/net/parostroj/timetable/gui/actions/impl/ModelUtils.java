package net.parostroj.timetable.gui.actions.impl;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Locale;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.model.ls.LSLibrary;
import net.parostroj.timetable.model.ls.LSLibraryFactory;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for handling model.
 *
 * @author jub
 */
public class ModelUtils {

    private static final Logger log = LoggerFactory.getLogger(ModelUtils.class);

    private ModelUtils() {}

    public static void saveModelData(final ApplicationModel model, File file) throws LSException {
        // update author and date before save
        TrainDiagram diagram = model.getDiagram();
        boolean originalSkip = diagram.getAttributes().isSkipListeners();
        diagram.getAttributes().setSkipListeners(true);
        String user = model.getProgramSettings().getUserNameOrSystemUser();
        diagram.setSaveUser(user);

        final ChangesTracker tracker = diagram.getChangesTracker();
        final DiagramChangeSet set = tracker.getCurrentChangeSet();
        if (set != null && tracker.isTrackingEnabled()) {
            try {
                // do the update in event dispatch thread (because of events)
                SwingUtilities.invokeAndWait(() -> tracker.updateCurrentChangeSet(set.getVersion(),
                        user,
                        Calendar.getInstance()));
            } catch (InterruptedException e) {
                log.warn("Error updating values for current diagram change set.", e);
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                log.warn("Error updating values for current diagram change set.", e);
            }
        }

        log.info("Saving: {}", file);
        LSFile ls = LSFileFactory.getInstance().createForSave();
        ls.save(diagram, file);
        diagram.getAttributes().setSkipListeners(originalSkip);
    }

    public static void saveLibraryData(final Library library, File file) throws LSException, IOException {
        LSLibrary ls = LSLibraryFactory.getInstance().createForSave();
        try (ZipOutputStream os = new ZipOutputStream(new FileOutputStream(file))) {
            boolean originalSkip = library.getAttributes().isSkipListeners();
            library.getAttributes().setSkipListeners(true);
            log.info("Saving: {}", file);
            ls.save(library, os);
            library.getAttributes().setSkipListeners(originalSkip);
        }
    }

    public static int checkModelChangedContinue(ApplicationModel model, Component parent) {
        if (!model.isModelChanged()) {
            return JOptionPane.NO_OPTION;
        } else {
            int result = JOptionPane.showConfirmDialog(parent, ResourceLoader.getString("model.not.saved.question"),ResourceLoader.getString("model.not.saved"),JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION && model.getOpenedFile() == null) {
                try (CloseableFileChooser modelFileChooser = FileChooserFactory.getInstance()
                        .getFileChooser(FileChooserFactory.Type.GTM)) {
                    int retVal = modelFileChooser.showSaveDialog(parent);
                    if (retVal == JFileChooser.APPROVE_OPTION) {
                        model.setOpenedFile(modelFileChooser.getSelectedFile());
                    } else {
                        result = JOptionPane.CANCEL_OPTION;
                    }
                }
            }
            return result;
        }
    }

    public static Locale parseLocale(String localeString) {
        return Locale.forLanguageTag(localeString);
    }
}
