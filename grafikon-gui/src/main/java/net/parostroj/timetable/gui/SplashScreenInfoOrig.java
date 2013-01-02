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

    private static final Logger LOG = LoggerFactory.getLogger(SplashScreenInfoOrig.class.getName());
    private int x,  y;
    private SplashScreen splash;

    public SplashScreenInfoOrig(SplashScreen splash, int x, int y) {
        this.splash = splash;
        this.x = x;
        this.y = y;
    }

    @Override
    public void setText(String text) {
        Graphics2D g = (Graphics2D) splash.createGraphics();
        Dimension size = splash.getSize();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, size.width, size.height);
        g.setPaintMode();
        if (text != null) {
            g.setFont(g.getFont().deriveFont(12.0f).deriveFont(Font.BOLD));
            String[] texts = text.split("\n");
            int posY = y;
            int incY = (int)g.getFont().getStringBounds("YMHC", g.getFontRenderContext()).getHeight() + 3;
            for (String str : texts) {
                g.setFont(g.getFont().deriveFont(12.0f).deriveFont(Font.BOLD));
                g.setColor(Color.BLACK);
                LOG.trace(String.format("Text %d,%d,%s", x, posY, str));
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
