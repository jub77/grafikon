package net.parostroj.timetable.utils;

import java.awt.Color;
import java.io.*;

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

    public static boolean compareWithNull(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 != null && o1.equals(o2)) {
            return true;
        } else {
            return false;
        }
    }

    public static String loadFile(InputStream is) throws IOException {
        StringBuilder b = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            b.append(line).append('\n');
        }
        return b.toString();
    }
}
