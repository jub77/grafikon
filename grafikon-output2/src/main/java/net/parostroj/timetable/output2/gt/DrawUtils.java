package net.parostroj.timetable.output2.gt;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.util.Arrays;

import com.google.common.base.Function;

/**
 * @author jub
 */
public final class DrawUtils {

    private DrawUtils() {}

    private static class ShortenStringFunction implements Function<Integer, String> {

        private final String str;

        public ShortenStringFunction(String str) {
            this.str = str;
        }

        @Override
        public String apply(Integer length) {
            return length > str.length() ? "" : str.substring(0, str.length() - length);
        }
    }

    public static FontInfo createFontInfo(Graphics2D g) {
        return createFontInfo(g.getFont(), g);
    }

    public static FontInfo createFontInfo(Font font, Graphics2D g) {
        return createFontInfo(font, g, "0");
    }

    public static FontInfo createFontInfo(Font font, Graphics2D g, String s) {
        LineMetrics lm = font.getLineMetrics(s, g.getFontRenderContext());
        return new FontInfo(Math.round(lm.getStrikethroughOffset()),
                Math.round(lm.getDescent()),
                Math.round(lm.getHeight()));
    }

    public static class FontInfo {
        public final int strikeThrough;
        public final int descent;
        public final int height;

        public FontInfo(int strikeThrough, int descent, int height) {
            this.strikeThrough = strikeThrough;
            this.descent = descent;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format("strike=%d, descent=%d, height=%d", strikeThrough, descent, height);
        }
    }

    /**
     * Returns string for given width. If the string is shortened, it adds three dots at the end.
     *
     * @param g graphics
     * @param str string
     * @param width desired width
     * @return string with maximum length givem by width
     */
    public static String getStringForWidth(Graphics2D g, String str, int width) {
        return getStringForWidth(g, new ShortenStringFunction(str), width, "...");
    }

    /**
     * Returns string for given width.
     *
     * @param g graphics
     * @param strFunc function creating string (increasing int parameter returns increasingly shorter string)
     * @param width desired width
     * @param suffix suffix added after string
     * @return string with maximum length defined by <code>width</code>
     */
    public static String getStringForWidth(Graphics2D g, Function<Integer, String> strFunc, int width, String suffix) {
        int shortening = 0;
        String result = strFunc.apply(shortening);
        String transStr = result;
        int strLength = -1;
        boolean found = false;
        while (!found) {
            int w = getStringWidth(g, transStr);
            if (w >= width) {
                shortening++;
                transStr = strFunc.apply(shortening);
                transStr += suffix;
                if (transStr.length() == strLength) {
                    result = transStr;
                    found = true;
                } else {
                    strLength = transStr.length();
                }
            } else {
                result = transStr;
                found = true;
            }
        }
        return result;
    }

    /**
     * Returns width of the string.
     *
     * @param g graphics
     * @param str string
     * @return its length
     */
    public static int getStringWidth(Graphics2D g, String str) {
        return g.getFontMetrics().stringWidth(str);
    }

    /**
     * Returns width of string for given font.
     *
     * @param g graphics
     * @param font font
     * @param str string
     * @return its length
     */
    public static int getStringWidth(Graphics2D g, Font font, String str) {
        return g.getFontMetrics(font).stringWidth(str);
    }

    public static float[] zoomDashes(float[] dashes, float zoom, float lRatio) {
        float[] newDashes = Arrays.copyOf(dashes, dashes.length);
        for (int i = 0; i < newDashes.length; i++) {
            newDashes[i] = newDashes[i] * zoom * lRatio;
        }
        return newDashes;
    }
}
