package net.parostroj.timetable.output2.gt;

import java.awt.Color;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Settings for GTDraw.
 *
 * @author jub
 */
public class GTDrawSettings {

    public static enum Key {
        ARRIVAL_DEPARTURE_DIGITS(Boolean.class),
        EXTENDED_LINES(Boolean.class),
        TRAIN_NAMES(Boolean.class),
        TECHNOLOGICAL_TIME(Boolean.class),
        BORDER_X(Float.class),
        BORDER_Y(Float.class),
        SIZE(Dimension.class),
        STATION_NAME_WIDTH(Integer.class),
        TRAIN_COLORS(GTDraw.TrainColors.class),
        START_TIME(Integer.class),
        END_TIME(Integer.class),
        DISABLE_STATION_NAMES(Boolean.class),
        ZOOM(Float.class),
        STATION_NAME_WIDTH_FIXED(Boolean.class),
        TITLE(Boolean.class),
        LEGEND(Boolean.class),
        INNER_SIZE(Boolean.class),
        TITLE_TEXT(String.class),
        BACKGROUND_COLOR(Color.class),
        ORIENTATION(GTOrientation.class),
        TRAIN_ENDS(Boolean.class),
        LOCALE(Locale.class);

        private final Class<?> valueClass;

        private Key(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }
    }

    protected Map<Key, Object> preferences;

    protected GTDrawSettings() {
        preferences = new EnumMap<>(Key.class);
    }

    public GTDrawSettings set(Key key, Object value) {
        if (value != null && !key.getValueClass().isInstance(value)) {
            throw new IllegalArgumentException(String.format("Wrong class of parameter: %s, should be %s", value.getClass(), key.getValueClass()));
        }
        preferences.put(key, value);
        return this;
    }

    public GTDrawSettings setRemove(Key key, Object value) {
        if (value == null) {
            this.remove(key);
        } else {
            this.set(key, value);
        }
        return this;
    }

    public GTDrawSettings remove(Key key) {
        preferences.remove(key);
        return this;
    }

    public Object get(Key key) {
        return preferences.get(key);
    }

    public <T> T get(Key key, Class<T> clazz) {
        return clazz.cast(preferences.get(key));
    }

    public boolean contains(Key key) {
        return preferences.containsKey(key);
    }

    public Boolean getOption(Key pref) {
        if (Boolean.class.equals(pref.getValueClass())) {
            return this.get(pref, Boolean.class);
        } else {
            throw new IllegalArgumentException("Option has to be boolean.");
        }
    }

    public boolean isOption(Key pref) {
        Boolean value = this.getOption(pref);
        return value != null ? value.booleanValue() : false;
    }

    public void setOption(Key pref, Boolean value) {
        this.set(pref, value);
    }

    public static GTDrawSettings create() {
        GTDrawSettings settings = new GTDrawSettings()
            .set(Key.BORDER_X, 1.5f)
            .set(Key.BORDER_Y, 1.5f)
            .set(Key.STATION_NAME_WIDTH, 15)
            .set(Key.TRAIN_COLORS, GTDraw.TrainColors.BY_TYPE)
            .set(Key.TRAIN_NAMES, Boolean.TRUE)
            .set(Key.ARRIVAL_DEPARTURE_DIGITS, Boolean.FALSE)
            .set(Key.EXTENDED_LINES, Boolean.FALSE)
            .set(Key.TECHNOLOGICAL_TIME, Boolean.FALSE)
            .set(Key.ZOOM, 1.0f)
            .set(Key.SIZE, new Dimension(640, 480))
            .set(Key.START_TIME, 0)
            .set(Key.END_TIME, TimeInterval.DAY)
            .set(Key.BACKGROUND_COLOR, Color.white)
            .set(Key.ORIENTATION, GTOrientation.LEFT_RIGHT)
            .set(Key.TRAIN_ENDS, Boolean.TRUE);
        return settings;
    }

    public static GTDrawSettings copy(GTDrawSettings settings) {
        GTDrawSettings copiedSettings = new GTDrawSettings();
        for (Entry<Key, Object> entry : settings.preferences.entrySet()) {
            copiedSettings.preferences.put(entry.getKey(), entry.getValue());
        }
        return copiedSettings;
    }

    public GTDrawSettings copy() {
        return copy(this);
    }
}
