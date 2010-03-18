package net.parostroj.timetable.gui;

import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Application starter class. It creates splash screen and after initialization
 * it starts application.
 * 
 * @author jub
 */
public class ApplicationStarter {
    private static final Logger LOG = Logger.getLogger(ApplicationStarter.class.getName());
    
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
            LOG.finer("Start starter.");
            if (SplashScreen.getSplashScreen() == null)
                startFrame();
            else {
                startOriginal();
            }
            LOG.finer("End starter.");
    }
    
    private JFrame getApplicationInstance(SplashScreenInfo splash) throws ApplicationStarterException {
        try {
            return applicationClass.getConstructor(SplashScreenInfo.class).newInstance(splash);
        } catch (NoSuchMethodException e) {
            try {
                return applicationClass.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(ApplicationStarter.class.getName()).log(Level.SEVERE, null, ex);
                throw new ApplicationStarterException(ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ApplicationStarter.class.getName()).log(Level.SEVERE, null, ex);
                throw new ApplicationStarterException(ex);
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ApplicationStarter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApplicationStarterException(ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ApplicationStarter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApplicationStarterException(ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ApplicationStarter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApplicationStarterException(ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ApplicationStarter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApplicationStarterException(ex);
        }
    }
    
    private void startOriginal() throws ApplicationStarterException {
        LOG.fine("Using Java 1.6 splash screen.");
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
        LOG.fine("Showing JFrame splash screen.");
        final SplashScreenFrame spl = new SplashScreenFrame(x, y, image);
        spl.setVisible(true);
        LOG.finer("Splash initialized.");
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
