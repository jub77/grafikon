package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
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
}
