package net.parostroj.timetable.gui.actions;

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
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.changes.ChangesTracker;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for handling model.
 *
 * @author jub
 */
public class ModelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUtils.class.getName());

    public static void saveModelData(final ApplicationModel model, File file) throws LSException {
        // update author and date before save
        final ChangesTracker tracker = model.getDiagram().getChangesTracker();
        final DiagramChangeSet set = tracker.getCurrentChangeSet();
        if (set != null && tracker.isTrackingEnabled()) {
            try {
                // do the update in event dispatch thread (because of events)
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        tracker.updateCurrentChangeSet(set.getVersion(),
                                model.getProgramSettings().getUserNameOrSystemUser(),
                                Calendar.getInstance());
                    }
                });
            } catch (Exception e) {
                LOG.warn("Error updating values for current diagram change set.", e);
            }
        }
        FileLoadSave ls = LSFileFactory.getInstance().createLatestForSave();
        ls.save(model.getDiagram(), file);
    }

    public static int checkModelChangedContinue(ApplicationModel model, Component parent) {
        if (!model.isModelChanged())
            return JOptionPane.NO_OPTION;
        else {
            int result = JOptionPane.showConfirmDialog(parent, ResourceLoader.getString("model.not.saved.question"),ResourceLoader.getString("model.not.saved"),JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION && model.getOpenedFile() == null) {
                JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
                int retVal = xmlFileChooser.showSaveDialog(parent);
                if (retVal == JFileChooser.APPROVE_OPTION)
                    model.setOpenedFile(xmlFileChooser.getSelectedFile());
                else
                    result = JOptionPane.CANCEL_OPTION;
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
                return diagram.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE);
            case ENGINE_CYCLE:
                return diagram.getCycles(TrainsCycleType.ENGINE_CYCLE);
            case DRIVER_CYCLE:
                return diagram.getCycles(TrainsCycleType.DRIVER_CYCLE);
            case TRAIN:
                return diagram.getTrains();
            case ROUTE:
                List<Route> routes = new LinkedList<Route>();
                for (Route r : diagram.getRoutes())
                    if (r.isNetPart())
                        routes.add(r);
                return routes;
        }
        return null;
    }

    public static Locale parseLocale(String localeString) {
        Locale returnedLocale = null;
        if (localeString != null) {
            String parts[] = localeString.split("_");
            if (parts.length == 1) {
                returnedLocale = new Locale(parts[0]);
            } else if (parts.length == 2) {
                returnedLocale = new Locale(parts[0],parts[1]);
            } else if (parts.length == 3) {
                returnedLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }
        return returnedLocale;
    }
}
