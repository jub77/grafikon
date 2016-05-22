package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.zip.ZipInputStream;

/**
 * Factory for loading/saving train diagram.
 *
 * @author jub
 */
public class LSFileFactory {

    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final LSFileFactory instance = new LSFileFactory();
    private static boolean initialized = false;

    private LSCache<LSFile> lsFileCache;

    public static synchronized LSFileFactory getInstance() {
        if (!initialized) {
            instance.init();
            initialized = true;
        }
        return instance;
    }

    private synchronized void init() {
        lsFileCache = new LSCache<>(LSFile.class, METADATA_KEY_MODEL_VERSION);
    }

    public synchronized LSFile createLatestForSave() throws LSException {
        return lsFileCache.createLatestForSave();
    }

    public synchronized LSFile createForLoad(ZipInputStream is) throws LSException {
        return lsFileCache.createForLoad(is);
    }

    public synchronized LSFile createForLoad(File file) throws LSException {
        return lsFileCache.createForLoad(file);
    }

    public synchronized LSFile createForLoad(ModelVersion modelVersion) throws LSException {
        return lsFileCache.createForLoad(modelVersion);
    }

    public synchronized LSFile createForLoad(String modelVersion) throws LSException {
        return this.createForLoad(ModelVersion.parseModelVersion(modelVersion));
    }
}
