package net.parostroj.timetable.gui;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splash frame.
 *
 * @author jub
 */
public class SplashScreenFrame extends JFrame implements SplashScreenInfo {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(SplashScreenFrame.class);

    private Dimension splSize;
    private ImagePanel imagePanel;

    private SplashScreenFrame() {
        this.setUndecorated(true);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
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
            log.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
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

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ImagePanel.class);

    private final transient Image image;
    private final int splashX;
    private final int splashY;
    private String[] text;

    public ImagePanel(int x, int y, Image image) {
        this.image = image;
        this.splashX = x;
        this.splashY = y;
    }

    public void setText(String text) {
        this.text = text != null ? text.split("\n") : null;
    }

    @Override
    public void paint(Graphics g) {
        log.trace("Splash paint start.");
        super.paint(g);
        g.drawImage(image, 0, 0, null);

        Graphics2D g2d = (Graphics2D)g;

        if (text != null) {
            g.setFont(new Font("SansSerif", Font.BOLD, 11).deriveFont(11.5f));
            g.setColor(Color.BLACK);
            int posY = splashY;
            int incY = (int)g.getFont().getStringBounds("YMHC", g2d.getFontRenderContext()).getHeight() + 3;
            for (String str : text) {
                log.trace("Text {},{},{}", splashX, posY, str);
                g.drawString(str, splashX, posY);
                posY += incY;
            }
        }
        log.trace("Splash paint end.");
    }
}
