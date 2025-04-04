package net.parostroj.timetable.gui.ini;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

public class IniConfigSectionCommons implements IniConfigSection {

    private final HierarchicalConfiguration<ImmutableNode> ini;
    private final String escpapedSectionKey;

    public IniConfigSectionCommons(HierarchicalConfiguration<ImmutableNode> ini, String sectionKey) {
        this.ini = ini;
        this.escpapedSectionKey = escapeKey(sectionKey);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return ini.get(clazz, escapeKey(escpapedSectionKey, key));
    }

    @Override
    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return ini.get(clazz, escapeKey(escpapedSectionKey, key), defaultValue);
    }

    @Override
    public <T> List<T> getAll(String key, Class<T> clazz) {
        return ini.getList(clazz, escapeKey(escpapedSectionKey, key));
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
    public Collection<String> getKeys() {
        Iterable<String> itarable = ini.subset(escpapedSectionKey)::getKeys;
        return StreamSupport.stream(itarable.spliterator(), false)
                .map(IniConfigSectionCommons::unescapeKey)
                .toList();
    }

    @Override
    public void add(String key, Object value) {
        ini.addProperty(escapeKey(escpapedSectionKey, key), value);
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
