package net.parostroj.timetable.output2.gt;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;

/**
 * @author jub
 */
public class DrawUtils {

    public static FontInfo createFontInfo(Font font, Graphics2D g) {
        LineMetrics lm = font.getLineMetrics("0", g.getFontRenderContext());
        FontInfo fontInfo = new FontInfo(Math.round(lm.getStrikethroughOffset()),
                Math.round(lm.getDescent()),
                Math.round(lm.getHeight()));
        return fontInfo;
    }

    public static class FontInfo {
        int strikeThrough;
        int descent;
        int height;

        public FontInfo(int strikeThrough, int descent, int height) {
            this.strikeThrough = strikeThrough;
            this.descent = descent;
            this.height = height;
        }
    }

    public static String getStringForWidth(Graphics2D g, String str, int width) {
        String result = str;
        String transStr = str;
        int strLength = transStr.length();
        boolean found = false;
        while (!found) {
            int w = getStringWidth(g, transStr);
            if (w >= width) {
                strLength -= 1;
                transStr = str.substring(0, strLength);
                transStr += "...";
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
}
