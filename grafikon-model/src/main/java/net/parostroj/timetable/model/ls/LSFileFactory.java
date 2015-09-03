package net.parostroj.timetable.model.ls;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSFileFactory {

    private static final Logger log = LoggerFactory.getLogger(LSFileFactory.class);

    private static final String METADATA = "metadata.properties";
    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final LSFileFactory instance = new LSFileFactory();
    private static final Map<ModelVersion, Class<? extends FileLoadSave>> cacheLoad = new ConcurrentHashMap<ModelVersion, Class<? extends FileLoadSave>>();
    private static final Map<ModelVersion, Class<? extends FileLoadSave>> cacheSave = new ConcurrentHashMap<ModelVersion, Class<? extends FileLoadSave>>();
    private static boolean initialized = false;

    public static synchronized LSFileFactory getInstance() {
        if (!initialized) {
            ServiceLoader<FileLoadSave> loader = ServiceLoader.load(FileLoadSave.class);
            for (FileLoadSave fls : loader) {
                List<ModelVersion> versions = fls.getLoadVersions();
                log.debug("Registered: {}", fls.getClass().getName());
                for (ModelVersion version : versions) {
                    cacheLoad.put(version, fls.getClass());
                }
                if (fls.getSaveVersion() != null) {
                    cacheSave.put(fls.getSaveVersion(), fls.getClass());
                }
            }
            initialized = true;
        }
        return instance;
    }

    public synchronized FileLoadSave createLatestForSave() throws LSException {
        ModelVersion latestVersion = this.getLatestSaveVersion();
        return latestVersion != null ? this.createFLSInstanceForSave(latestVersion) : null;
    }

    public synchronized FileLoadSave createForSave(ModelVersion modelVersion) throws LSException {
        return this.createFLSInstanceForSave(modelVersion);
    }

    private ModelVersion getLatestSaveVersion() {
        Map.Entry<ModelVersion, Class<? extends FileLoadSave>> selected = null;
        for (Map.Entry<ModelVersion, Class<? extends FileLoadSave>> entry : cacheSave.entrySet()) {
            if (selected == null || entry.getKey().compareTo(selected.getKey()) > 0) {
                selected = entry;
            }
        }
        return selected != null ? selected.getKey() : null;
    }

    public synchronized FileLoadSave createForLoad(ZipInputStream is) throws LSException {
        try {
            ZipEntry entry = is.getNextEntry();
            if (entry == null || !entry.getName().equals(METADATA)) {
                throw new LSException("Metadata was not the first entry.");
            }
            Properties metadata = new Properties();
            if (entry != null) {
                // load metadata
                metadata.load(is);
            }
            return this.createFLSInstanceForLoad(metadata);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    public synchronized FileLoadSave createForLoad(File file) throws LSException {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry entry = zipFile.getEntry(METADATA);
            Properties metadata = new Properties();
            if (entry != null) {
                // load metadata
                metadata.load(zipFile.getInputStream(entry));
            }

            return this.createFLSInstanceForLoad(metadata);
        } catch (ZipException ex) {
            throw new LSException(ex);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    public synchronized FileLoadSave createForLoad(ModelVersion modelVersion) throws LSException {
        return this.createFLSInstanceForLoad(modelVersion);
    }

    public synchronized FileLoadSave createForLoad(String modelVersion) throws LSException {
        return this.createFLSInstanceForLoad(ModelVersion.parseModelVersion(modelVersion));
    }

    private FileLoadSave createFLSInstanceForLoad(Properties metadata) throws LSException {
        // set model version
        ModelVersion modelVersion = null;
        if (metadata.getProperty(METADATA_KEY_MODEL_VERSION) == null) {
            modelVersion = ModelVersion.parseModelVersion("1.0");
        } else {
            modelVersion = ModelVersion.parseModelVersion(metadata.getProperty(METADATA_KEY_MODEL_VERSION));
        }

        return this.createFLSInstanceForLoad(modelVersion);
    }

    private FileLoadSave createFLSInstanceForLoad(ModelVersion modelVersion) throws LSException {
        try {
            Class<? extends FileLoadSave> clazz = cacheLoad.get(modelVersion);
            if (clazz == null)
                throw new LSException("No FileLoadSave registered for version: " + modelVersion.getVersion());
            return new FileLoadSaveLoadWrapper(clazz.newInstance());
        } catch (InstantiationException ex) {
            throw new LSException(ex);
        } catch (IllegalAccessException ex) {
            throw new LSException(ex);
        }
    }

    private FileLoadSave createFLSInstanceForSave(ModelVersion modelVersion) throws LSException {
        try {
            Class<? extends FileLoadSave> clazz = cacheSave.get(modelVersion);
            if (clazz == null)
                throw new LSException("No FileLoadSave registered for version: " + modelVersion.getVersion());
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new LSException(ex);
        } catch (IllegalAccessException ex) {
            throw new LSException(ex);
        }
    }
}
