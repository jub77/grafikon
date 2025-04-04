package net.parostroj.timetable.gui.ini;

import java.util.Collection;
import java.util.List;

public interface IniConfigSection {

    default String get(String key) {
        return this.get(key, String.class);
    }

    default String get(String key, String defaultValue) {
        return this.get(key, String.class, defaultValue);
    }

    default List<String> getAll(String key) {
        return getAll(key, String.class);
    }

    <T> T get(String key, Class<T> clazz, T defaultValue);

    <T> T get(String key, Class<T> clazz);

    <T> List<T> getAll(String key, Class<T> clazz);

    void removeSection();

    void clear();

    Collection<String> getKeys();

    void remove(String key);

    void add(String key, Object value);

    void put(String key, Object value);

    boolean containsKey(String key);
}
