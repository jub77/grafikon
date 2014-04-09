package net.parostroj.timetable.model;

import java.util.ResourceBundle;

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

    public String getName() {
        return getText(key);
    }

    public String getAbbr() {
        return getText(key + ".abbr");
    }

    public static FreightColor getByKey(String key) {
        for (FreightColor color : values()) {
            if (color.getKey().equals(key)) {
                return color;
            }
        }
        return null;
    }

    private static String getText(String key) {
        return ResourceBundle.getBundle("net.parostroj.timetable.model.color_texts").getString(key);
    }
}
