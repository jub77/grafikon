package net.parostroj.timetable.model.ls;

import java.util.function.UnaryOperator;

/**
 * Abstract LSFactory.
 *
 * @author jub
 */
class AbstractLSFactory<T extends LSVersions> {

    private final LSCache<T> lsFileCache;

    public AbstractLSFactory(Class<T> cacheType, String versionKey, String metadataFile, UnaryOperator<T> loadWrapper) {
        lsFileCache = new LSCache<>(cacheType, versionKey, metadataFile, loadWrapper);
    }

    public T createForSave() throws LSException {
        return lsFileCache.createLatestForSave();
    }

    public T createForLoad(LSSource source) throws LSException {
        return lsFileCache.createForLoad(source);
    }

    public T createForLoad(ModelVersion modelVersion) throws LSException {
        return lsFileCache.createForLoad(modelVersion);
    }

    public T createForLoad(String modelVersion) throws LSException {
        return this.createForLoad(ModelVersion.parseModelVersion(modelVersion));
    }
}
