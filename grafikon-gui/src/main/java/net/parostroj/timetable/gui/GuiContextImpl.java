package net.parostroj.timetable.gui;

import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;

/**
 * Helper class for window information.
 *
 * @author jub
 */
public class GuiContextImpl implements GuiContext, StorableGuiData {

    private static final String INI_SECTION = "windows";

    private final Map<String, StorableWindowData> dataMap = new HashMap<>();
    private final Map<String, Map<String, String>> preferencesMap = new HashMap<>();

    // reference to ini
    private IniConfig preferences;

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection(INI_SECTION);
        dataMap.forEach((key, value) -> section.put(key, this.dataToString(value)));
        preferencesMap.forEach((key, value) -> {
            IniConfigSection windowSection = prefs.getSection(key);
            if (value == null) {
                windowSection.removeSection();
            } else {
                windowSection.clear();
                windowSection.putAll(value);
            }
        });
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection(INI_SECTION);
        section.entrySet().forEach(
                entry -> dataMap.put(entry.getKey(), this.dataFromString(entry.getValue())));

        this.preferences = prefs;

        return section;
    }

    @Override
    public void registerWindow(String key, Window window, GuiContextDataListener listener) {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                putToDataMap(key, window, listener);
            }
        });
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                putToDataMap(key, window, listener);
            }
        });
        if (dataMap.containsKey(key)) {
            dataMap.get(key).applyTo(window);
        }
        if (listener != null) {
            Map<String, String> map;
            if (preferencesMap.containsKey(key)) {
                map = preferencesMap.get(key);
            } else {
                map = new HashMap<>();
                preferences.getSection(key).copyToMap(map);
            }
            if (map == null) {
                map = Collections.emptyMap();
            }
            listener.init(map);
        }
    }

    private void putToDataMap(String key, Window window, GuiContextDataListener listener) {
        dataMap.put(key, StorableWindowData.getFrom(window));
        if (listener != null) {
            Map<String, String> map = listener.save();
            // store map if non-empty
            if (map.isEmpty()) {
                preferencesMap.put(key, null);
            } else {
                preferencesMap.put(key, map);
            }
        }
    }

    private String dataToString(StorableWindowData data) {
        return String.format("%d|%d|%d|%d", data.getX(), data.getY(), data.getWidth(), data.getHeight());
    }

    private StorableWindowData dataFromString(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, "|");
        List<Integer> values = new LinkedList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            values.add(Integer.valueOf(token));
        }
        return new StorableWindowData(values.get(0), values.get(1), values.get(2), values.get(3));
    }
}
