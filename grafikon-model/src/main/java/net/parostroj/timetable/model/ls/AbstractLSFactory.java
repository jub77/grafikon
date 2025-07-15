package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.function.UnaryOperator;
import java.util.zip.ZipInputStream;

/**
 * Abstract LSFactory.
 *
 * @author jub
 */
class AbstractLSFactory<T extends LS<V>, V> implements LSFactory<T, V> {

    private final LSCache<T> lsFileCache;

    public AbstractLSFactory(Class<T> cacheType, String versionKey, String metadataFile, UnaryOperator<T> loadWrapper) {
        lsFileCache = new LSCache<>(cacheType, versionKey, metadataFile, loadWrapper);
    }

    @Override
    public T createForSave() throws LSException {
        return lsFileCache.createLatestForSave();
    }

    @Override
    public T createForLoad() throws LSException {
        return lsFileCache.createLatestForLoad();
    }

    @Override
    public T createForLoad(ZipInputStream is) throws LSException {
        return lsFileCache.createForLoad(is);
    }

    @Override
    public T createForLoad(File file) throws LSException {
        return lsFileCache.createForLoad(file);
    }

    @Override
    public T createForLoad(ModelVersion modelVersion) throws LSException {
        return lsFileCache.createForLoad(modelVersion);
    }
}
