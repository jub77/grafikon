package net.parostroj.timetable.utils;

import java.awt.Color;

/**
 * Converting utility.
 *
 * @author jub
 */
public class Conversions {

    public static Color convertTextToColor(String text) {
        return Color.decode(text);
    }

    public static String convertColorToText(Color c) {
        return String.format("0x%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static String checkAndTrim(String str) {
        if (str != null) {
            str = str.trim();
            if ("".equals(str)) {
                str = null;
            }
        }
        return str;
    }
}
