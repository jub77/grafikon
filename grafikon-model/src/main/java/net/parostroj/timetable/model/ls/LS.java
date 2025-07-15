package net.parostroj.timetable.model.ls;

/**
 * Interface for generic load/save.
 *
 * @param <T> type of object to load/save
 */
public interface LS<T> extends LSVersions {

    T load(LSSource source, LSFeature... features) throws LSException;

    void save(T object, LSSink sink) throws LSException;
}
