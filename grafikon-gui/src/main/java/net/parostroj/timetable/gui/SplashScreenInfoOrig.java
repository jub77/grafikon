package net.parostroj.timetable.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of splash screen info for splash screen from java 1.6.
 *
 * @author jub
 */
public class SplashScreenInfoOrig implements SplashScreenInfo {

    private static final Logger log = LoggerFactory.getLogger(SplashScreenInfoOrig.class);

    private final int x;
    private final int y;
    private final SplashScreen splash;

    public SplashScreenInfoOrig(SplashScreen splash, int x, int y) {
        this.splash = splash;
        this.x = x;
        this.y = y;
    }

    @Override
    public void setText(String text) {
        Graphics2D g = splash.createGraphics();
        Dimension size = splash.getSize();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, size.width, size.height);
        g.setPaintMode();
        if (text != null) {
            g.setFont(new Font("SansSerif", Font.BOLD, 11).deriveFont(11.5f));
            String[] texts = text.split("\n");
            g.setColor(Color.BLACK);
            int posY = y;
            int incY = (int)g.getFont().getStringBounds("YMHC", g.getFontRenderContext()).getHeight() + 3;
            for (String str : texts) {
                log.trace("Text {},{},{}", x, posY, str);
                g.drawString(str, x, posY);
                posY += incY;
            }
        }
        splash.update();
    }

    @Override
    public void setProgress(int progress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
