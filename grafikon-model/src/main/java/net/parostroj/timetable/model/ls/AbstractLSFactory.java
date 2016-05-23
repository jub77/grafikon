package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.zip.ZipInputStream;

/**
 * Abstract LSFactory.
 *
 * @author jub
 */
class AbstractLSFactory<T extends LSVersions> {

    private final LSCache<T> lsFileCache;

    public AbstractLSFactory(Class<T> cacheType, String versionKey, String metadataFile) {
        lsFileCache = new LSCache<>(cacheType, versionKey, metadataFile);
    }

    public T createForSave() throws LSException {
        return lsFileCache.createLatestForSave();
    }

    public T createForLoad(ZipInputStream is) throws LSException {
        return lsFileCache.createForLoad(is);
    }

    public T createForLoad(File file) throws LSException {
        return lsFileCache.createForLoad(file);
    }

    public T createForLoad(ModelVersion modelVersion) throws LSException {
        return lsFileCache.createForLoad(modelVersion);
    }

    public T createForLoad(String modelVersion) throws LSException {
        return this.createForLoad(ModelVersion.parseModelVersion(modelVersion));
    }
}
