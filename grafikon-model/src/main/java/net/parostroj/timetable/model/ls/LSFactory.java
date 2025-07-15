package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.zip.ZipInputStream;

/**
 * Factory for LS.
 *
 * @param <T> type of LS
 * @param <V> type of object handled by LS
 */
public interface LSFactory<T extends LS<V>, V> {

    T createForSave() throws LSException;

    T createForLoad(ZipInputStream is) throws LSException;

    T createForLoad(File file) throws LSException;

    T createForLoad(ModelVersion modelVersion) throws LSException;

    T createForLoad() throws LSException;
}
