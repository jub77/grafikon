package net.parostroj.timetable.utils;

import java.awt.Color;
import java.io.*;

import com.google.common.io.ByteSource;
import java.nio.charset.StandardCharsets;

/**
 * Converting utility.
 *
 * @author jub
 */
public final class Conversions {

    private Conversions() {}

    public static Color convertTextToColor(String text) {
        return Color.decode(text);
    }

    public static String convertColorToText(Color c) {
        return String.format("0x%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static String loadFile(final InputStream is) throws IOException {
        return new ByteSource() {
            @Override
            public InputStream openStream() {
                return is;
            }
        }.asCharSource(StandardCharsets.UTF_8).read();
    }
}
