package net.parostroj.timetable.gui.actions;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * File chooser factory. Instances of the same type are shared, so do not use
 * simultanously the instance of file chooser in two places at the same time.
 * 
 * @author jub
 */
public class FileChooserFactory {

    public enum Type {
        OUTPUT, GTM, OUTPUT_DIRECTORY, TEMPLATE;
    }

    public static final String FILE_EXTENSION = "gtm";
    private static final FileChooserFactory INSTANCE = new FileChooserFactory();
    private static final Logger LOG = Logger.getLogger(FileChooserFactory.class.getName());
    private JFileChooser outputFileChooserInstance;
    private JFileChooser gtmFileChooserInstance;
    private JFileChooser outputDirectoryFileChooserInstance;
    private JFileChooser templateFileChooserInstance;

    public static FileChooserFactory getInstance() {
        return INSTANCE;
    }

    public JFileChooser getFileChooser(Type type) {
        switch (type) {
            case OUTPUT_DIRECTORY:
                if (outputDirectoryFileChooserInstance == null) {
                    outputDirectoryFileChooserInstance = new JFileChooser();
                    outputDirectoryFileChooserInstance.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // last directory
                    try {
                        String lastDir = AppPreferences.getPreferences().getString("last.directory.html.dir", null);
                        if (lastDir != null) {
                            outputDirectoryFileChooserInstance.setCurrentDirectory(new File(lastDir));
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Cannot get last directory from preferences.", e);
                    }
                }
                return outputDirectoryFileChooserInstance;
            case OUTPUT:
                if (outputFileChooserInstance == null) {
                    outputFileChooserInstance = new ApprovedFileChooser(ResourceLoader.getString("output.html"), "html");
                    setLastDirectory(outputFileChooserInstance, "last.directory.output");
                }
                return outputFileChooserInstance;
            case TEMPLATE:
                if (templateFileChooserInstance == null) {
                    templateFileChooserInstance = new ApprovedFileChooser(ResourceLoader.getString("output.template"), "gsp");
                    setLastDirectory(templateFileChooserInstance, "last.directory.template");
                }
                return templateFileChooserInstance;
            case GTM:
                if (gtmFileChooserInstance == null) {
                    gtmFileChooserInstance = new ApprovedFileChooser(ResourceLoader.getString("files.description"), FILE_EXTENSION);
                    setLastDirectory(gtmFileChooserInstance, "last.directory.model");
                }
                return gtmFileChooserInstance;
        }
        return null;
    }

    public JFileChooser getFileChooser(Type type, String suffix) {
        JFileChooser fileChooser = this.getFileChooser(type);
        if (fileChooser instanceof ApprovedFileChooser) {
            ApprovedFileChooser aFileChooser = (ApprovedFileChooser)fileChooser;
            aFileChooser.setSuffix(suffix);
        }
        return fileChooser;
    }

    public JFileChooser getFileChooser(Type type, String suffix, String description) {
        JFileChooser fileChooser = this.getFileChooser(type);
        if (fileChooser instanceof ApprovedFileChooser) {
            ApprovedFileChooser aFileChooser = (ApprovedFileChooser)fileChooser;
            aFileChooser.setSuffix(description, suffix);
        }
        return fileChooser;
    }

    private void setLastDirectory(JFileChooser chooser, String key) {
        // last directory
        try {
            String lastDir = AppPreferences.getPreferences().getString(key, null);
            if (lastDir != null) {
                chooser.setCurrentDirectory(new File(lastDir));
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Cannot get last directory from preferences.", e);
        }

    }

    public void saveToPreferences(AppPreferences prefs) {
        prefs.setString("last.directory.model",
                this.getFileChooser(Type.GTM).getCurrentDirectory().getAbsolutePath());
        prefs.setString("last.directory.template",
                this.getFileChooser(Type.TEMPLATE).getCurrentDirectory().getAbsolutePath());
        prefs.setString("last.directory.output",
                this.getFileChooser(Type.OUTPUT).getCurrentDirectory().getAbsolutePath());
        prefs.setString("last.directory.html.dir",
                this.getFileChooser(Type.OUTPUT_DIRECTORY).getCurrentDirectory().getAbsolutePath());
    }
}
