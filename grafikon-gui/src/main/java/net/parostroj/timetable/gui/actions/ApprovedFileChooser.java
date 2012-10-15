package net.parostroj.timetable.gui.actions;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * File chooser with approval for overwriting a file.
 *
 * @author jub
 */
public class ApprovedFileChooser extends JFileChooser {

    private String suffix;
    private String description;

    public ApprovedFileChooser(String description, String suffix) {
        this.setSuffix(description, suffix);
    }

    @Override
    public void approveSelection() {
        if (getDialogType() == JFileChooser.SAVE_DIALOG) {
            if (!this.getSelectedFile().getName().toLowerCase().endsWith("." + suffix)) {
                this.setSelectedFile(new File(this.getSelectedFile().getAbsolutePath() + "." + suffix));
            }
        }
        if ((getDialogType() == JFileChooser.SAVE_DIALOG) && getSelectedFile().exists()) {
            int result = JOptionPane.showConfirmDialog(this,
                    String.format(ResourceLoader.getString("savedialog.overwrite.text"), getSelectedFile()),
                    ResourceLoader.getString("savedialog.overwrite.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        super.approveSelection();
    }

    public void setSuffix(String description, String suffix) {
        // do not change existing one, if it is the same
        if (suffix.equals(this.suffix))
            return;
        this.suffix = suffix;
        this.description = description;
        // file extension filter
        this.resetChoosableFileFilters();
        this.setFileFilter(new FileNameExtensionFilter(description, suffix));
    }

    public void setSuffix(String suffix) {
        this.setSuffix(this.description, suffix);
    }
}
