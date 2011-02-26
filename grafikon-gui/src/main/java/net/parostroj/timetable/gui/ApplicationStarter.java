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
public class ApplicationStarter<T extends JFrame> {
    
    public static interface AfterStartAction<T> {

        public void action(T frame);
    }

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStarter.class.getName());
    
    private Class<T> applicationClass;
    private Image image;
    private int x,y;
    private AfterStartAction<T> action;
    
    private ApplicationStarter(Class<T> applicationClass, int x, int y) {
        this.applicationClass = applicationClass;
        this.x = x;
        this.y =y;
    }

    public ApplicationStarter(Class<T> applicationClass, int x, int y, Image image) {
        this(applicationClass, x, y);
        this.image = image;
    }
    
    public ApplicationStarter(Class<T> applicationClass, int x, int y, URL url) {
        this(applicationClass, x, y);
        this.image = this.loadImage(url);
    }
    
    public void setAction(AfterStartAction<T> action) {
        this.action = action;
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
    
    private T getApplicationInstance(SplashScreenInfo splash) throws ApplicationStarterException {
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
        final T frm = this.getApplicationInstance(info);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frm.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        if (action != null)
                            action.action(frm);
                    }
                });
            }
        });
    }
    
    private void startFrame() throws ApplicationStarterException {
        LOG.debug("Showing JFrame splash screen.");
        final SplashScreenFrame spl = new SplashScreenFrame(x, y, image);
        spl.setVisible(true);
        LOG.trace("Splash initialized.");
        final T frm = this.getApplicationInstance(spl);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                spl.setVisible(false);
                spl.dispose();
                frm.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        if (action != null)
                            action.action(frm);
                    }
                });
            }
        });
    }
}
