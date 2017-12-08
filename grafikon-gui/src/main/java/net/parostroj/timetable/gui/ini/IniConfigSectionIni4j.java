package net.parostroj.timetable.gui.ini;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

public class IniConfigSectionIni4j implements IniConfigSection {

    private final Ini ini;
    private final Section section;

    public IniConfigSectionIni4j(Ini ini, Section section) {
        this.ini = ini;
        this.section = section;
    }

    @Override
    public void put(String key, String value) {
        section.put(key, value);
    }

    @Override
    public String get(String key) {
        return section.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return section.get(key, clazz, defaultValue);
    }

    @Override
    public void removeSection() {
        ini.remove(section);
    }

    @Override
    public void clear() {
        section.clear();
    }

    @Override
    public void putAll(Map<String, String> value) {
        section.putAll(value);
    }

    @Override
    public Collection<Entry<String, String>> entrySet() {
        return section.entrySet();
    }

    @Override
    public void copyToMap(Map<String, String> map) {
        map.putAll(section);
    }

    @Override
    public void add(String key, String value) {
        section.add(key, value);
    }

    @Override
    public String get(String key, String defaultValue) {
        return section.get(key, defaultValue);
    }

    @Override
    public List<String> getAll(String key) {
        return section.getAll(key);
    }

    @Override
    public void put(String key, Object value) {
        section.put(key, value);
    }

    @Override
    public void remove(String key) {
        section.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        return section.containsKey(key);
    }
}
