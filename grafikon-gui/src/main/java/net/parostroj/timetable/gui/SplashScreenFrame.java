package net.parostroj.timetable.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Splash frame.
 * 
 * @author jub
 */
public class SplashScreenFrame extends JFrame implements SplashScreenInfo {

    private static final Logger LOG = Logger.getLogger(SplashScreenFrame.class.getName());
    private Dimension splSize;
    private ImagePanel imagePanel;

    private SplashScreenFrame() {
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
    }

    public SplashScreenFrame(int x, int y, Image image) {
        this();
        try {
            MediaTracker t = new MediaTracker(this);
            t.addImage(image, 1);
            t.waitForID(1);
            splSize = new Dimension(image.getWidth(null), image.getHeight(null));

            imagePanel = new ImagePanel(x, y, image);

            getContentPane().add(imagePanel);

            this.setSplashPosition();
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void setSplashPosition() {
        this.setSize(splSize);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - splSize.width) / 2, (screenSize.height - splSize.height) / 2);
    }

    @Override
    public void setText(String text) {
        imagePanel.setText(text);
        imagePanel.repaint();
    }

    @Override
    public void setProgress(int progress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

class ImagePanel extends JPanel {

    private static final Logger LOG = Logger.getLogger(ImagePanel.class.getName());
    private Image image;
    private int x;
    private int y;
    private String[] text;

    public ImagePanel(int x, int y, Image image) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.text = text != null ? text.split("\n") : null;
    }

    @Override
    public void paint(Graphics g) {
        LOG.finer("Splash paint start.");
        super.paint(g);
        g.drawImage(image, 0, 0, null);

        Graphics2D g2d = (Graphics2D)g;

        if (text != null) {
            int posY = y;
            int incY = (int)g.getFont().getStringBounds("YMHC", g2d.getFontRenderContext()).getHeight() + 3;
            for (String str : text) {
                g.setFont(g.getFont().deriveFont(12.0f).deriveFont(Font.BOLD));
                g.setColor(Color.BLACK);
                LOG.finest(String.format("Text %d,%d,%s", x, posY, str));
                g.drawString(str, x, posY);
                posY += incY;
            }
        }
        LOG.finer("Splash paint end.");
    }
}
