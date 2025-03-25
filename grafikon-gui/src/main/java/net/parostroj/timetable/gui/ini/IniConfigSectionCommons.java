package net.parostroj.timetable.gui.ini;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IniConfigSectionCommons implements IniConfigSection {

    private final HierarchicalConfiguration<ImmutableNode> ini;
    private final String escpapedSectionKey;

    public IniConfigSectionCommons(HierarchicalConfiguration<ImmutableNode> ini, String sectionKey) {
        this.ini = ini;
        this.escpapedSectionKey = escapeKey(sectionKey);
    }

    @Override
    public void put(String key, String value) {
        ini.setProperty(escapeKey(escpapedSectionKey, key), value);
    }

    @Override
    public String get(String key) {
        return ini.getString(escapeKey(escpapedSectionKey, key));
    }

    @Override
    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return ini.get(clazz, escapeKey(escpapedSectionKey, key), defaultValue);
    }

    @Override
    public void removeSection() {
        ini.clearTree(escpapedSectionKey);
    }

    @Override
    public void clear() {
        removeSection();
    }

    @Override
    public void putAll(Map<String, String> values) {
        values.forEach((key, value) -> ini.setProperty(escapeKey(escpapedSectionKey, key), value));
    }

    @Override
    public Collection<Entry<String, String>> entrySet() {
        Map<String, String> map = new HashMap<>();
        copyToMap(map);
        return map.entrySet();
    }

    @Override
    public void copyToMap(Map<String, String> map) {
        Iterable<String> i = ini.subset(escpapedSectionKey)::getKeys;
        for (String escapedKey : i) {
            map.put(unescapeKey(escapedKey), ini.getString(escpapedSectionKey + "." + escapedKey));
        }
    }

    @Override
    public void add(String key, String value) {
        ini.addProperty(escapeKey(escpapedSectionKey, key), value);
    }

    @Override
    public String get(String key, String defaultValue) {
        return ini.getString(escapeKey(escpapedSectionKey, key), defaultValue);
    }

    @Override
    public List<String> getAll(String key) {
        return ini.getList(String.class, escapeKey(escpapedSectionKey, key));
    }

    @Override
    public void put(String key, Object value) {
        ini.setProperty(escapeKey(escpapedSectionKey, key), value);
    }

    @Override
    public void remove(String key) {
        ini.clearProperty(escapeKey(escpapedSectionKey, key));
    }

    @Override
    public boolean containsKey(String key) {
        return ini.containsKey(escapeKey(escpapedSectionKey, key));
    }

    static String escapeKey(String key) {
        return key.replace(".", "..");
    }

    static String unescapeKey(String key) {
        return key.replace("..", ".");
    }

    static String escapeKey(String escpapedSectionKey, String key) {
        return escpapedSectionKey + "." + escapeKey(key);
    }
}
