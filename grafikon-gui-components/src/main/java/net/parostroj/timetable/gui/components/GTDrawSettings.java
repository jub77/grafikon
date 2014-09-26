package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Map;

import net.parostroj.timetable.gui.components.GTViewSettings.TrainColors;

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
        STATION_GAP_X(Integer.class),
        TRAIN_COLORS(TrainColors.class),
        TRAIN_COLOR_CHOOSER(TrainColorChooser.class),
        HIGHLIGHTED_TRAINS(HighlightedTrains.class),
        START_TIME(Integer.class),
        END_TIME(Integer.class),
        DISABLE_STATION_NAMES(Boolean.class),
        ZOOM(Float.class);

        private final Class<?> valueClass;

        private Key(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }
    }

    protected Map<Key, Object> preferences;

    public GTDrawSettings() {
        preferences = new EnumMap<Key, Object>(Key.class);
    }

    public GTDrawSettings set(Key key, Object value) {
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

    public boolean contains(Key key) {
        return preferences.containsKey(key);
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
}
