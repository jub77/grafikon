package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Helper methods for handling model.
 *
 * @author jub
 */
public class ModelUtils {

    public static void saveModelData(ApplicationModel model, File file) throws LSException {
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
        }
        return null;
    }
}
