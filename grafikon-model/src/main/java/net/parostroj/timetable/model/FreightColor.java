package net.parostroj.timetable.model;

import java.util.Locale;

import net.parostroj.timetable.utils.ResourceBundleUtil;

/**
 * Freight color.
 *
 * @author jub
 */
public enum FreightColor {

    RED(1, "red"), BLUE(2, "blue"), BLACK(3, "black"), BROWN(4, "brown"), ORANGE(5, "orange"), YELLOW(6, "yellow"), GREEN(7, "green");

    private int intKey;
    private String key;

    private FreightColor(int intKey, String key) {
        this.key = key;
        this.intKey = intKey;
    }

    public int getIntKey() {
        return intKey;
    }

    public String getKey() {
        return key;
    }

    public String getName(Locale locale) {
        return getText(key, locale);
    }

    public String getAbbr(Locale locale) {
        return getText(key + ".abbr", locale);
    }

    public String getName() {
        return getName(Locale.getDefault());
    }

    public String getAbbr() {
        return getAbbr(Locale.getDefault());
    }

    public static FreightColor getByKey(String key) {
        for (FreightColor color : values()) {
            if (color.getKey().equals(key)) {
                return color;
            }
        }
        return null;
    }

    private static String getText(String key, Locale locale) {
        return ResourceBundleUtil.getBundle("net.parostroj.timetable.model.color_texts", FreightColor.class.getClassLoader(), locale, Locale.ENGLISH).getString(key);
    }
}
