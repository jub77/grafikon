package net.parostroj.timetable.gui.components;

import java.util.EnumMap;
import java.util.Map;
import net.parostroj.timetable.gui.components.GraphicalTimetableView.Type;

/**
 * Settings for graphical timetable view.
 *
 * @author jub
 */
public class GTViewSettings {
    private GraphicalTimetableView.Type type;
    private Map<GTDrawPreference, Boolean> options;
    private int size;

    public GTViewSettings() {
        options = new EnumMap<GTDrawPreference, Boolean>(GTDrawPreference.class);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean getOption(GTDrawPreference pref) {
        return options.get(pref);
    }

    public void setOption(GTDrawPreference pref, Boolean value) {
        options.put(pref, value);
    }

    public String getStorageString() {
        return String.format("%s,%s,%s,%s,%s,%s", type.name(), Integer.toString(size),
                getOption(GTDrawPreference.TRAIN_NAMES).toString(),
                getOption(GTDrawPreference.ARRIVAL_DEPARTURE_DIGITS).toString(),
                getOption(GTDrawPreference.EXTENDED_LINES).toString(),
                getOption(GTDrawPreference.TECHNOLOGICAL_TIME).toString());
    }

    public static GTViewSettings parseStorageString(String str) {
        GTViewSettings settings = new GTViewSettings();
        String[] split = str.split(",");
        settings.setType(Type.valueOf(split[0]));
        settings.setSize(Integer.parseInt(split[1]));
        settings.setOption(GTDrawPreference.TRAIN_NAMES, Boolean.parseBoolean(split[2]));
        settings.setOption(GTDrawPreference.ARRIVAL_DEPARTURE_DIGITS, Boolean.parseBoolean(split[3]));
        settings.setOption(GTDrawPreference.EXTENDED_LINES, Boolean.parseBoolean(split[4]));
        settings.setOption(GTDrawPreference.TECHNOLOGICAL_TIME, Boolean.parseBoolean(split[5]));
        return settings;
    }
}
