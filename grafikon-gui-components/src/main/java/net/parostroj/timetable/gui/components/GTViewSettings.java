package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Map;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.output2.gt.*;

/**
 * Settings for graphical timetable view.
 *
 * @author jub
 */
public class GTViewSettings {

    public static enum Key {
        ARRIVAL_DEPARTURE_DIGITS(Boolean.class, GTDrawSettings.Key.ARRIVAL_DEPARTURE_DIGITS),
        EXTENDED_LINES(Boolean.class, GTDrawSettings.Key.EXTENDED_LINES),
        TRAIN_NAMES(Boolean.class, GTDrawSettings.Key.TRAIN_NAMES),
        TECHNOLOGICAL_TIME(Boolean.class, GTDrawSettings.Key.TECHNOLOGICAL_TIME),
        BORDER_X(Float.class, GTDrawSettings.Key.BORDER_X),
        BORDER_Y(Float.class, GTDrawSettings.Key.BORDER_Y),
        SIZE(Dimension.class, GTDrawSettings.Key.SIZE),
        VIEW_SIZE(Integer.class, null),
        STATION_GAP_X(Integer.class, GTDrawSettings.Key.STATION_GAP_X),
        TYPE(GTDraw.Type.class, null),
        TRAIN_COLORS(GTDraw.TrainColors.class, GTDrawSettings.Key.TRAIN_COLORS),
        TRAIN_COLOR_CHOOSER(TrainColorChooser.class, GTDrawSettings.Key.TRAIN_COLOR_CHOOSER),
        HIGHLIGHTED_TRAINS(HighlightedTrains.class, GTDrawSettings.Key.HIGHLIGHTED_TRAINS),
        START_TIME(Integer.class, GTDrawSettings.Key.START_TIME),
        END_TIME(Integer.class, GTDrawSettings.Key.END_TIME),
        IGNORE_TIME_LIMITS(Boolean.class, null),
        DISABLE_STATION_NAMES(Boolean.class, GTDrawSettings.Key.DISABLE_STATION_NAMES),
        ZOOM(Float.class, GTDrawSettings.Key.ZOOM),
        START_TIME_OVERRIDE(Integer.class, null),
        END_TIME_OVERRIDE(Integer.class, null),
        TO_TRAIN_SCROLL(Boolean.class, null),
        TO_TRAIN_CHANGE_ROUTE(Boolean.class, null);

        private Class<?> valueClass;
        private GTDrawSettings.Key drawKey;

        private Key(Class<?> valueClass, GTDrawSettings.Key drawKey) {
            this.valueClass = valueClass;
            this.drawKey = drawKey;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

        public GTDrawSettings.Key getDrawKey() {
            return drawKey;
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

    public GTViewSettings setRemove(Key key, Object value) {
        if (value == null) {
            this.remove(key);
        } else {
            this.set(key, value);
        }
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

    public boolean contains(Key key) {
        return preferences.containsKey(key);
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

    public GTDrawSettings createGTDrawSettings() {
        GTDrawSettings ds = GTDrawSettings.create();
        for (GTViewSettings.Key gKey : GTViewSettings.Key.values()) {
            if (gKey.getDrawKey() != null && this.contains(gKey)) {
                ds.set(gKey.getDrawKey(), this.get(gKey));
            }
        }
        // update start/end time
        if (this.contains(Key.START_TIME_OVERRIDE)) {
            ds.set(GTDrawSettings.Key.START_TIME, this.get(Key.START_TIME_OVERRIDE));
        }
        if (this.contains(Key.END_TIME_OVERRIDE)) {
            ds.set(GTDrawSettings.Key.END_TIME, this.get(Key.END_TIME_OVERRIDE));
        }
        if (this.getOption(Key.IGNORE_TIME_LIMITS)) {
            ds.set(GTDrawSettings.Key.START_TIME, 0);
            ds.set(GTDrawSettings.Key.END_TIME, TimeInterval.DAY);
        }
        if (ds.get(GTDrawSettings.Key.START_TIME) == null) {
            ds.set(GTDrawSettings.Key.START_TIME, 0);
        }
        if (ds.get(GTDrawSettings.Key.END_TIME) == null) {
            ds.set(GTDrawSettings.Key.END_TIME, TimeInterval.DAY);
        }
        return ds;
    }

    public GTDraw.Type getGTDrawType() {
        return this.get(Key.TYPE, GTDraw.Type.class);
    }

    public String getStorageString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", get(Key.TYPE), get(Key.VIEW_SIZE),
                getOption(Key.TRAIN_NAMES).toString(),
                getOption(Key.ARRIVAL_DEPARTURE_DIGITS).toString(),
                getOption(Key.EXTENDED_LINES).toString(),
                getOption(Key.TECHNOLOGICAL_TIME).toString(),
                getOption(Key.IGNORE_TIME_LIMITS).toString(),
                get(Key.ZOOM).toString(),
                getOption(Key.TO_TRAIN_SCROLL).toString(),
                getOption(Key.TO_TRAIN_CHANGE_ROUTE).toString()
            );
    }

    public static GTViewSettings parseStorageString(String str) {
        GTViewSettings settings = new GTViewSettings();
        if (str != null) {
            String[] split = str.split(",");
            settings.set(Key.TYPE, GTDraw.Type.valueOf(split[0]));
            settings.set(Key.VIEW_SIZE, Integer.parseInt(split[1]));
            settings.setOption(Key.TRAIN_NAMES, Boolean.parseBoolean(split[2]));
            settings.setOption(Key.ARRIVAL_DEPARTURE_DIGITS, Boolean.parseBoolean(split[3]));
            settings.setOption(Key.EXTENDED_LINES, Boolean.parseBoolean(split[4]));
            settings.setOption(Key.TECHNOLOGICAL_TIME, Boolean.parseBoolean(split[5]));
            if (split.length > 6) {
                settings.setOption(Key.IGNORE_TIME_LIMITS, Boolean.parseBoolean(split[6]));
            }
            if (split.length > 7) {
                settings.set(Key.ZOOM, Float.parseFloat(split[7]));
            }
            if (split.length > 8) {
                settings.setOption(Key.TO_TRAIN_SCROLL, Boolean.parseBoolean(split[8]));
                settings.setOption(Key.TO_TRAIN_CHANGE_ROUTE, Boolean.parseBoolean(split[9]));
            }
        }
        return settings;
    }
}
