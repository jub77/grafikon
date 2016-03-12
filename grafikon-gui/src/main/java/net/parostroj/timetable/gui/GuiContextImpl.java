package net.parostroj.timetable.gui;

import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

/**
 * Helper class for window information.
 *
 * @author jub
 */
public class GuiContextImpl implements GuiContext, StorableGuiData {

    private static final String INI_SECTION = "windows";

    private final Map<String, StorableWindowData> dataMap = new HashMap<>();

    @Override
    public Section saveToPreferences(Ini prefs) {
        Section section = AppPreferences.getSection(prefs, INI_SECTION);
        dataMap.entrySet().forEach(entry -> {
            section.put(entry.getKey(), this.dataToString(entry.getValue()));
        });
        return section;
    }

    @Override
    public Section loadFromPreferences(Ini prefs) {
        Section section = AppPreferences.getSection(prefs, INI_SECTION);
        section.entrySet().stream().forEach(entry -> {
            dataMap.put(entry.getKey(), this.dataFromString(entry.getValue()));
        });

        return section;
    }

    @Override
    public void registerWindow(String key, Window window) {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                dataMap.put(key, StorableWindowData.getFrom(window));
            }
        });
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                dataMap.put(key, StorableWindowData.getFrom(window));
            }
        });
        if (dataMap.containsKey(key)) {
            dataMap.get(key).applyTo(window);
        }
    }

    private String dataToString(StorableWindowData data) {
        return String.format("%d|%d|%d|%d", data.getX(), data.getY(), data.getWidth(), data.getHeight());
    }

    private StorableWindowData dataFromString(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, "|");
        List<Integer> values = new LinkedList<Integer>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            values.add(Integer.valueOf(token));
        }
        return new StorableWindowData(values.get(0), values.get(1), values.get(2), values.get(3));
    }
}
