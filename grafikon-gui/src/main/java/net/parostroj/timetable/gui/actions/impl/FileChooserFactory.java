package net.parostroj.timetable.gui.actions.impl;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * File chooser factory. Instances of the same type are shared, so do not use
 * simultanously the instance of file chooser in two places at the same time.
 *
 * @author jub
 */
public class FileChooserFactory implements StorableGuiData {

    public static final String LAST_DIRECTORY_MODEL_KEY = "last.directory.model";

    public enum Type {
        GTM(LAST_DIRECTORY_MODEL_KEY),
        OUTPUT_DIRECTORY("last.directory.output", true),
        ALL_FILES("last.directory.all.files"),
        GTML(LAST_DIRECTORY_MODEL_KEY),
        GTM_GTML(LAST_DIRECTORY_MODEL_KEY);

        private final boolean directoryOnly;
        private final String key;

        Type(String key) {
            this(key, false);
        }

        Type(String key, boolean directoryOnly) {
            this.key = key;
            this.directoryOnly = directoryOnly;
        }

        public boolean isDirectoryOnly() {
            return directoryOnly;
        }

        public String getKey() {
            return key;
        }
    }

    private class Configuration {

        private final Type type;
        private final boolean directoryOnly;
        private final FileNameExtensionFilter filter;

        public Configuration(Type type, String description, String... extensions) {
            this.type = type;
            this.directoryOnly = false;
            this.filter = new FileNameExtensionFilter(description, extensions);
        }

        public Configuration(Type type, boolean directoryOnly) {
            this.type = type;
            this.directoryOnly = directoryOnly;
            this.filter = null;
        }

        public Type getType() {
            return type;
        }

        public void initializeChooser(ApprovedFileChooser chooser) {
            File location = locations.get(type.getKey());
            // switch to directory selection to remove selected file (setSelected(null) does not work)
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (location != null) {
                chooser.setCurrentDirectory(location);
            }
            chooser.setFileSelectionMode(directoryOnly ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
            chooser.setSelectedFile(null);
            if (filter != null) {
                chooser.addChoosableFileFilter(filter);
                chooser.setFileFilter(filter);
            }
        }

        public void cleanUpChooser(ApprovedFileChooser chooser) {
            File location = chooser.getCurrentDirectory();
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile != null && selectedFile.isDirectory()) {
                location = selectedFile;
            }
            locations.put(type.getKey(), location);
            if (filter != null) {
                chooser.removeChoosableFileFilter(filter);
            }
        }
    }

    private static final FileChooserFactory INSTANCE = new FileChooserFactory();

    private final ChooserPool pool;
    private final Map<Type, Configuration> configurations;
    private final Map<String, File> locations;

    private FileChooserFactory() {
        configurations = new EnumMap<>(Type.class);
        this.addConfiguration(new Configuration(Type.ALL_FILES, false));
        this.addConfiguration(new Configuration(Type.OUTPUT_DIRECTORY, true));
        this.addConfiguration(new Configuration(Type.GTM, ResourceLoader.getString("file.gtm"), "gtm"));
        this.addConfiguration(new Configuration(Type.GTML, ResourceLoader.getString("file.gtml"), "gtml"));
        this.addConfiguration(new Configuration(Type.GTM_GTML, ResourceLoader.getString("file.gtm.gtml"), "gtm","gtml"));
        locations = new HashMap<>();
        pool = new ChooserPool();
    }

    private void addConfiguration(Configuration config) {
        configurations.put(config.getType(), config);
    }

    public static FileChooserFactory getInstance() {
        return INSTANCE;
    }

    public CloseableFileChooser getFileChooser(Type type) {
        final ApprovedFileChooser chooser = pool.getChooser();
        Configuration config = configurations.get(type);
        config.initializeChooser(chooser);
        chooser.setCloseAction(ch -> {
            config.cleanUpChooser((ApprovedFileChooser) ch);
            pool.returnChooser((ApprovedFileChooser) ch);
        });
        return chooser;
    }

    public File getLocation(Type type) {
        File file = locations.get(type.getKey());
        if (file == null) {
            file = new File(".");
        }
        return file;
    }

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("main");
        locations.forEach((key, value) -> section.put(key, value.getAbsolutePath()));
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("main");
        Set<String> keys = new HashSet<>();
        for (Type type : Type.values()) {
            keys.add(type.getKey());
        }
        for (String key : keys) {
            String value = section.get(key);
            if (value != null) {
                locations.put(key, new File(value));
            }
        }
        return section;
    }

    private static class ChooserPool {

        private static final int CAPACITY = 3;

        private final Deque<SoftReference<ApprovedFileChooser>> choosers;

        public ChooserPool() {
            choosers = new LinkedList<>();
        }

        public ApprovedFileChooser getChooser() {
            ApprovedFileChooser chooser = choosers.isEmpty() ? null : choosers.pop().get();
            if (chooser == null) {
                chooser = new ApprovedFileChooser();
            }
            return chooser;
        }

        public void returnChooser(ApprovedFileChooser chooser) {
            if (choosers.size() < CAPACITY) {
                choosers.push(new SoftReference<>(chooser));
            }
        }
    }

    public void initialize() {
        ApprovedFileChooser chooser = pool.getChooser();
        pool.returnChooser(chooser);
    }
}
