package net.parostroj.timetable.model.ls;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LSCache<T extends LSVersions> {

    private static final String METADATA = "metadata.properties";

    private static final Logger log = LoggerFactory.getLogger(LSCache.class);

    private final Map<ModelVersion, Class<? extends LSVersions>> cacheLoad = new ConcurrentHashMap<ModelVersion, Class<? extends LSVersions>>();
    private final Map<ModelVersion, Class<? extends LSVersions>> cacheSave = new ConcurrentHashMap<ModelVersion, Class<? extends LSVersions>>();

    private final Class<T> cacheType;
    private final String versionKey;

    public LSCache(Class<T> clazz, String versionKey) {
        this.cacheType = clazz;
        this.versionKey = versionKey;
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        for (T fls : loader) {
            List<ModelVersion> versions = fls.getLoadVersions();
            log.debug("[{}] Registered: {}", clazz.getSimpleName(), fls.getClass().getName());
            for (ModelVersion version : versions) {
                cacheLoad.put(version, fls.getClass());
            }
            if (fls.getSaveVersion() != null) {
                cacheSave.put(fls.getSaveVersion(), fls.getClass());
            }
        }
        log.debug("[{}] Latest: {}", clazz.getSimpleName(), getLatestSaveVersion());
    }

    public ModelVersion getLatestSaveVersion() {
        Entry<ModelVersion, Class<? extends LSVersions>> selected = null;
        for (Entry<ModelVersion, Class<? extends LSVersions>> entry : cacheSave.entrySet()) {
            if (selected == null || entry.getKey().compareTo(selected.getKey()) > 0) {
                selected = entry;
            }
        }
        return selected != null ? selected.getKey() : null;
    }

    public T createLatestForSave() throws LSException {
        ModelVersion latestVersion = this.getLatestSaveVersion();
        return latestVersion != null ? this.createInstanceForSave(latestVersion) : null;
    }

    public T createForLoad(ZipInputStream is) throws LSException {
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
            return this.createInstanceForLoad(metadata);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    public T createForLoad(File file) throws LSException {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry entry = zipFile.getEntry(METADATA);
            Properties metadata = new Properties();
            if (entry != null) {
                // load metadata
                metadata.load(zipFile.getInputStream(entry));
            }

            return this.createInstanceForLoad(metadata);
        } catch (ZipException ex) {
            throw new LSException(ex);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    public T createForLoad(ModelVersion modelVersion) throws LSException {
        return this.createInstanceForLoad(modelVersion);
    }

    private T createInstanceForSave(ModelVersion modelVersion) throws LSException {
        try {
            Class<? extends LSVersions> clazz = cacheSave.get(modelVersion);
            if (clazz == null)
                throw new LSException("No FileLoadSave registered for version: " + modelVersion.getVersion());
            return cacheType.cast(clazz.newInstance());
        } catch (InstantiationException ex) {
            throw new LSException(ex);
        } catch (IllegalAccessException ex) {
            throw new LSException(ex);
        }
    }

    private T createInstanceForLoad(Properties metadata) throws LSException {
        // set model version
        ModelVersion modelVersion = null;
        if (metadata.getProperty(versionKey) == null) {
            modelVersion = ModelVersion.parseModelVersion("1.0");
        } else {
            modelVersion = ModelVersion.parseModelVersion(metadata.getProperty(versionKey));
        }

        return this.createInstanceForLoad(modelVersion);
    }

    private T createInstanceForLoad(ModelVersion modelVersion) throws LSException {
        try {
            Class<? extends LSVersions> clazz = cacheLoad.get(modelVersion);
            if (clazz == null)
                throw new LSException("No FileLoadSave registered for version: " + modelVersion.getVersion());
            return cacheType.cast(clazz.newInstance());
        } catch (InstantiationException ex) {
            throw new LSException(ex);
        } catch (IllegalAccessException ex) {
            throw new LSException(ex);
        }
    }
}
