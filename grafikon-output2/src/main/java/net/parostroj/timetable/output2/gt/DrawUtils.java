package net.parostroj.timetable.output2.gt;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.util.Arrays;

import com.google.common.base.Function;

/**
 * @author jub
 */
public class DrawUtils {

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
        FontInfo fontInfo = new FontInfo(Math.round(lm.getStrikethroughOffset()),
                Math.round(lm.getDescent()),
                Math.round(lm.getHeight()));
        return fontInfo;
    }

    public static class FontInfo {
        public int strikeThrough;
        public int descent;
        public int height;

        public FontInfo(int strikeThrough, int descent, int height) {
            this.strikeThrough = strikeThrough;
            this.descent = descent;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d", strikeThrough, descent, height);
        }
    }

    public static String getStringForWidth(Graphics2D g, String str, int width) {
        return getStringForWidth(g, new ShortenStringFunction(str), width, "...");
    }

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

    public static int getStringWidth(Graphics2D g, String str) {
        return g.getFontMetrics().stringWidth(str);
    }

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
