package net.parostroj.timetable.gui.ini;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface IniConfigSection {

    void put(String key, String value);

    String get(String key);

    <T> T get(String key, Class<T> clazz, T defaultValue);

    void removeSection();

    void clear();

    void putAll(Map<String, String> value);

    Collection<Entry<String, String>> entrySet();

    void copyToMap(Map<String, String> map);

    void remove(String key);

    void add(String key, String value);

    String get(String key, String defaultValue);

    List<String> getAll(String key);

    void put(String key, Object value);

    boolean containsKey(String key);
}
