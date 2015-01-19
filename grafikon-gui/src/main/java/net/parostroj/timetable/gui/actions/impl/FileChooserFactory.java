package net.parostroj.timetable.gui.actions.impl;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.utils.ResourceLoader;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(FileChooserFactory.class);
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
                        String lastDir = AppPreferences.getSection("main").get("last.directory.html.dir");
                        if (lastDir != null) {
                            outputDirectoryFileChooserInstance.setCurrentDirectory(new File(lastDir));
                        }
                    } catch (IOException e) {
                        log.warn("Cannot get last directory from preferences.", e);
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
            Ini.Section section = AppPreferences.getSection("main");
            String lastDir = section.get(key);
            if (lastDir != null) {
                chooser.setCurrentDirectory(new File(lastDir));
            }
        } catch (IOException e) {
            log.warn("Cannot get last directory from preferences.", e);
        }

    }

    public void saveToPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, "main");
        section.put("last.directory.model",
                this.getFileChooser(Type.GTM).getCurrentDirectory().getAbsolutePath());
        section.put("last.directory.template",
                this.getFileChooser(Type.TEMPLATE).getCurrentDirectory().getAbsolutePath());
        section.put("last.directory.output",
                this.getFileChooser(Type.OUTPUT).getCurrentDirectory().getAbsolutePath());
        JFileChooser dChooser = this.getFileChooser(Type.OUTPUT_DIRECTORY);
        File oDir = dChooser.getSelectedFile() == null ? dChooser.getCurrentDirectory() : dChooser.getSelectedFile();
        section.put("last.directory.html.dir", oDir.getAbsolutePath());
    }
}
