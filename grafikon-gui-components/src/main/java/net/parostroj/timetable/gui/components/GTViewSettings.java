package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Map;

/**
 * Settings for graphical timetable view.
 *
 * @author jub
 */
public class GTViewSettings {

    public static enum Type {
        CLASSIC, WITH_TRACKS;
    }

    public static enum TrainColors {
        BY_TYPE, BY_COLOR_CHOOSER;
    }

    public static enum Selection {
        INTERVAL, TRAIN;
    }

    public static enum Key {
        ARRIVAL_DEPARTURE_DIGITS(Boolean.class),
        EXTENDED_LINES(Boolean.class),
        TRAIN_NAMES(Boolean.class),
        TECHNOLOGICAL_TIME(Boolean.class),
        BORDER_X(Integer.class),
        BORDER_Y(Integer.class),
        SIZE(Dimension.class),
        VIEW_SIZE(Integer.class),
        STATION_GAP_X(Integer.class),
        TYPE(Type.class),
        TRAIN_COLORS(TrainColors.class),
        SELECTION(Selection.class),
        TRAIN_COLOR_CHOOSER(TrainColorChooser.class),
        HIGHLIGHTED_TRAINS(HighlightedTrains.class),
        START_TIME(Integer.class),
        END_TIME(Integer.class),
        IGNORE_TIME_LIMITS(Boolean.class),
        DISABLE_STATION_NAMES(Boolean.class);

        private Class<?> valueClass;

        private Key(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }
    }

    protected Map<Key, Object> preferences;

    public GTViewSettings() {
        preferences = new EnumMap<Key, Object>(Key.class);
    }

    public GTViewSettings(GTViewSettings copied) {
        preferences = new EnumMap<Key, Object>(copied.preferences);
    }

    public GTViewSettings set(Key key, Object value) {
        if (value != null && !key.getValueClass().isInstance(value))
            throw new IllegalArgumentException("Wrong class of parameter.");
        preferences.put(key, value);
        return this;
    }

    public Object get(Key key) {
        return preferences.get(key);
    }

    public <T> T get(Key key, Class<T> clazz) {
        return clazz.cast(preferences.get(key));
    }

    public Object remove(Key key) {
        return preferences.remove(key);
    }

    public GTViewSettings merge(GTViewSettings merged) {
        for (Map.Entry<Key, Object> pair : merged.preferences.entrySet()) {
            preferences.put(pair.getKey(), pair.getValue());
        }
        return this;
    }

    public Boolean getOption(Key pref) {
        if (Boolean.class.equals(pref.getValueClass()))
            return this.get(pref, Boolean.class);
        else
            throw new IllegalArgumentException("Option has to be boolean.");
    }

    public void setOption(Key pref, Boolean value) {
        this.set(pref, value);
    }

    public String getStorageString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", get(Key.TYPE), get(Key.VIEW_SIZE),
                getOption(Key.TRAIN_NAMES).toString(),
                getOption(Key.ARRIVAL_DEPARTURE_DIGITS).toString(),
                getOption(Key.EXTENDED_LINES).toString(),
                getOption(Key.TECHNOLOGICAL_TIME).toString(),
                getOption(Key.IGNORE_TIME_LIMITS).toString());
    }

    public static GTViewSettings parseStorageString(String str) {
        GTViewSettings settings = new GTViewSettings();
        if (str != null) {
            String[] split = str.split(",");
            settings.set(Key.TYPE, Type.valueOf(split[0]));
            settings.set(Key.VIEW_SIZE, Integer.parseInt(split[1]));
            settings.setOption(Key.TRAIN_NAMES, Boolean.parseBoolean(split[2]));
            settings.setOption(Key.ARRIVAL_DEPARTURE_DIGITS, Boolean.parseBoolean(split[3]));
            settings.setOption(Key.EXTENDED_LINES, Boolean.parseBoolean(split[4]));
            settings.setOption(Key.TECHNOLOGICAL_TIME, Boolean.parseBoolean(split[5]));
            if (split.length > 6)
                settings.setOption(Key.IGNORE_TIME_LIMITS, Boolean.parseBoolean(split[6]));
        }
        return settings;
    }
}
