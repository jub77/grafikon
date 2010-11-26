package net.parostroj.timetable.gui;

import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application starter class. It creates splash screen and after initialization
 * it starts application.
 * 
 * @author jub
 */
public class ApplicationStarter {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStarter.class.getName());
    
    private Class<? extends JFrame> applicationClass;
    
    private Image image;
    
    private int x,y;
    
    private ApplicationStarter(Class<? extends JFrame> applicationClass, int x, int y) {
        this.applicationClass = applicationClass;
        this.x = x;
        this.y =y;
    }

    public ApplicationStarter(Class<? extends JFrame> applicationClass, int x, int y, Image image) {
        this(applicationClass, x, y);
        this.image = image;
    }
    
    public ApplicationStarter(Class<? extends JFrame> applicationClass, int x, int y, URL url) {
        this(applicationClass, x, y);
        this.image = this.loadImage(url);
    }
    
    private Image loadImage(URL url) {
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    
    public void start() throws ApplicationStarterException {
            LOG.trace("Start starter.");
            if (SplashScreen.getSplashScreen() == null)
                startFrame();
            else {
                startOriginal();
            }
            LOG.trace("End starter.");
    }
    
    private JFrame getApplicationInstance(SplashScreenInfo splash) throws ApplicationStarterException {
        try {
            return applicationClass.getConstructor(SplashScreenInfo.class).newInstance(splash);
        } catch (NoSuchMethodException e) {
            try {
                return applicationClass.newInstance();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ApplicationStarterException(ex);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ApplicationStarterException(ex);
        }
    }
    
    private void startOriginal() throws ApplicationStarterException {
        LOG.debug("Using Java 1.6 splash screen.");
        SplashScreen splash = SplashScreen.getSplashScreen();
        SplashScreenInfoOrig info = new SplashScreenInfoOrig(splash, x, y);
        final JFrame frm = this.getApplicationInstance(info);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frm.setVisible(true);
            }
        });
    }
    
    private void startFrame() throws ApplicationStarterException {
        LOG.debug("Showing JFrame splash screen.");
        final SplashScreenFrame spl = new SplashScreenFrame(x, y, image);
        spl.setVisible(true);
        LOG.trace("Splash initialized.");
        final JFrame frm = this.getApplicationInstance(spl);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                spl.setVisible(false);
                spl.dispose();
                frm.setVisible(true);
            }
        });
    }
}
