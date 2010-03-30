package net.parostroj.timetable.gui.actions;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * File chooser factory.
 * 
 * @author jub
 */
public class FileChooserFactory {

    public enum Type {
        OUTPUT, XML, ALL_HTML;
    }

    public static final String FILE_EXTENSION = "gtm";

    private static final FileChooserFactory INSTANCE = new FileChooserFactory();
    private static final Logger LOG = Logger.getLogger(FileChooserFactory.class.getName());

    private JFileChooser outputFileChooserInstance;
    private JFileChooser xmlFileChooserInstance;
    private JFileChooser allHtmlFileChooserInstance;

    public static FileChooserFactory getInstance() {
        return INSTANCE;
    }

    public synchronized JFileChooser getFileChooser(Type type) {
        switch (type) {
            case ALL_HTML:
                if (allHtmlFileChooserInstance == null) {
                    allHtmlFileChooserInstance = new JFileChooser();
                    allHtmlFileChooserInstance.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // last directory
                    try {
                        String lastDir = AppPreferences.getPreferences().getString("last.directory.html.dir", null);
                        if (lastDir != null) {
                            allHtmlFileChooserInstance.setCurrentDirectory(new File(lastDir));
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Cannot get last directory from preferences.", e);
                    }
                }
                return allHtmlFileChooserInstance;
            case OUTPUT:
                if (outputFileChooserInstance == null) {
                    outputFileChooserInstance = new JFileChooser() {
                        @Override
                        public void approveSelection() {
                            if (getDialogType() == JFileChooser.SAVE_DIALOG)
                                if (!this.getSelectedFile().getName().toLowerCase().endsWith(".html"))
                                    this.setSelectedFile(new File(this.getSelectedFile().getAbsolutePath() + ".html"));
                            if ((getDialogType() == JFileChooser.SAVE_DIALOG) && getSelectedFile().exists()) {
                                int result = JOptionPane.showConfirmDialog(this,
                                        String.format(ResourceLoader.getString("savedialog.overwrite.text"), getSelectedFile()),
                                        ResourceLoader.getString("savedialog.overwrite.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                                if(result != JOptionPane.YES_OPTION) {
                                    return;
                                }
                            }
                            super.approveSelection();
                        }
                    };
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(ResourceLoader.getString("output.html"), "html");
                    outputFileChooserInstance.setFileFilter(filter);
                    // last directory
                    try {
                        String lastDir = AppPreferences.getPreferences().getString("last.directory.output", null);
                        if (lastDir != null) {
                            outputFileChooserInstance.setCurrentDirectory(new File(lastDir));
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Cannot get last directory from preferences.", e);
                    }
                }
                return outputFileChooserInstance;
            case XML:
                if (xmlFileChooserInstance == null) {
                    xmlFileChooserInstance = new JFileChooser() {
                        @Override
                        public void approveSelection() {
                            if (getDialogType() == JFileChooser.SAVE_DIALOG)
                                if (!this.getSelectedFile().getName().toLowerCase().endsWith("." + FILE_EXTENSION))
                                    this.setSelectedFile(new File(this.getSelectedFile().getAbsolutePath()+"." + FILE_EXTENSION));
                            if ((getDialogType() == JFileChooser.SAVE_DIALOG) && getSelectedFile().exists()) {
                                int result = JOptionPane.showConfirmDialog(this,
                                        String.format(ResourceLoader.getString("savedialog.overwrite.text"), getSelectedFile()),
                                        ResourceLoader.getString("savedialog.overwrite.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                                if(result != JOptionPane.YES_OPTION) {
                                    return;
                                }
                            }
                            super.approveSelection();
                        }
                    };
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(ResourceLoader.getString("files.description"), FILE_EXTENSION);
                    xmlFileChooserInstance.setFileFilter(filter);
                    // last directory
                    try {
                        String lastDir = AppPreferences.getPreferences().getString("last.directory.model", null);
                        if (lastDir != null) {
                            xmlFileChooserInstance.setCurrentDirectory(new File(lastDir));
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Cannot get last directory from preferences.", e);
                    }
                }
                return xmlFileChooserInstance;
        }
        return null;
    }
}
