package net.parostroj.timetable.gui.actions.impl;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * File chooser with approval for overwriting a file.
 *
 * @author jub
 */
public class ApprovedFileChooser extends CloseableFileChooser {

    private boolean approve;

    public ApprovedFileChooser() {
        this.approve = true;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public boolean isApprove() {
        return approve;
    }

    @Override
    public void approveSelection() {
        if (approve) {
            if (getDialogType() == JFileChooser.SAVE_DIALOG) {
                checkAndFixExtension();
            }
            if ((getDialogType() == JFileChooser.SAVE_DIALOG) && getSelectedFile().exists()) {
                int result = JOptionPane.showConfirmDialog(this,
                        String.format(ResourceLoader.getString("savedialog.overwrite.text"), getSelectedFile()),
                        ResourceLoader.getString("savedialog.overwrite.confirmation"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
        super.approveSelection();
    }

    private void checkAndFixExtension() {
        FileFilter filter = getFileFilter();
        String suffix = filter instanceof FileNameExtensionFilter
                ? ((FileNameExtensionFilter) filter).getExtensions()[0] : null;
        if (suffix != null && !this.getSelectedFile().getName().toLowerCase().endsWith("." + suffix)) {
            this.setSelectedFile(new File(this.getSelectedFile().getAbsolutePath() + "." + suffix));
        }
    }
}
