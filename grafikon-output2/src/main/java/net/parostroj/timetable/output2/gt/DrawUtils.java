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

}
