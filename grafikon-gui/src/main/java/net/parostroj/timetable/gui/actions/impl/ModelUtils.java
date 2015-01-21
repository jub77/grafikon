package net.parostroj.timetable.gui.actions.impl;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.LocaleUtils;
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

    public static void saveModelData(final ApplicationModel model, File file) throws LSException {
        // update author and date before save
        final ChangesTracker tracker = model.getDiagram().getChangesTracker();
        final DiagramChangeSet set = tracker.getCurrentChangeSet();
        if (set != null && tracker.isTrackingEnabled()) {
            try {
                // do the update in event dispatch thread (because of events)
                SwingUtilities.invokeAndWait(() -> {
                    tracker.updateCurrentChangeSet(set.getVersion(),
                            model.getProgramSettings().getUserNameOrSystemUser(),
                            Calendar.getInstance());
                });
            } catch (Exception e) {
                log.warn("Error updating values for current diagram change set.", e);
            }
        }
        FileLoadSave ls = LSFileFactory.getInstance().createLatestForSave();
        ls.save(model.getDiagram(), file);
    }

    public static int checkModelChangedContinue(ApplicationModel model, Component parent) {
        if (!model.isModelChanged()) {
            return JOptionPane.NO_OPTION;
        } else {
            int result = JOptionPane.showConfirmDialog(parent, ResourceLoader.getString("model.not.saved.question"),ResourceLoader.getString("model.not.saved"),JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION && model.getOpenedFile() == null) {
                JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
                int retVal = xmlFileChooser.showSaveDialog(parent);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    model.setOpenedFile(xmlFileChooser.getSelectedFile());
                } else {
                    result = JOptionPane.CANCEL_OPTION;
                }
            }
            return result;
        }
    }

    public static List<? extends Object> selectAllElements(TrainDiagram diagram, ElementType type) {
        switch (type) {
            case NODE:
                return new ArrayList<Node>(diagram.getNet().getNodes());
            case LINE:
                return new ArrayList<Line>(diagram.getNet().getLines());
            case TRAIN_UNIT_CYCLE:
                return diagram.getTrainUnitCycles();
            case ENGINE_CYCLE:
                return diagram.getEngineCycles();
            case DRIVER_CYCLE:
                return diagram.getDriverCycles();
            case TRAIN:
                return diagram.getTrains();
            case ROUTE:
                List<Route> routes = new LinkedList<Route>();
                for (Route r : diagram.getRoutes()) {
                    if (r.isNetPart()) {
                        routes.add(r);
                    }
                }
                return routes;
            case CUSTOM_CYCLE:
                List<TrainsCycle> cycles = new LinkedList<TrainsCycle>();
                for (TrainsCycleType cycleType : diagram.getCycleTypes()) {
                    if (!cycleType.isDefaultType()) {
                        cycles.addAll(diagram.getCycles(cycleType));
                    }
                }
                return cycles;
        }
        return null;
    }

    public static Locale parseLocale(String localeString) {
        return LocaleUtils.parseLocale(localeString);
    }
}
