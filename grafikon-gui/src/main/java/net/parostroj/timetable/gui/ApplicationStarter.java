package net.parostroj.timetable.gui;

import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;

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

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);

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
            log.trace("Start starter.");
            if (SplashScreen.getSplashScreen() == null) {
                startFrame();
            } else {
                startOriginal();
            }
            log.trace("End starter.");
    }

    private T getApplicationInstance(SplashScreenInfo splash) throws ApplicationStarterException {
        try {
            return applicationClass.getConstructor(SplashScreenInfo.class).newInstance(splash);
        } catch (NoSuchMethodException e) {
            try {
                return applicationClass.newInstance();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new ApplicationStarterException(ex);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new ApplicationStarterException(ex);
        }
    }

    private void startOriginal() throws ApplicationStarterException {
        log.debug("Using Java 1.6 splash screen.");
        SplashScreen splash = SplashScreen.getSplashScreen();
        SplashScreenInfoOrig info = new SplashScreenInfoOrig(splash, x, y);
        final T frm = this.getApplicationInstance(info);
        GuiComponentUtils.runLaterInEDT(() -> {
            frm.setVisible(true);
            GuiComponentUtils.runLaterInEDT(() -> {
                if (action != null) {
                    action.action(frm);
                }
            });
        });
    }

    private void startFrame() throws ApplicationStarterException {
        log.debug("Showing JFrame splash screen.");
        final SplashScreenFrame spl = new SplashScreenFrame(x, y, image);
        spl.setVisible(true);
        log.trace("Splash initialized.");
        final T frm = this.getApplicationInstance(spl);
        GuiComponentUtils.runLaterInEDT(() -> {
            spl.setVisible(false);
            spl.dispose();
            frm.setVisible(true);
            GuiComponentUtils.runLaterInEDT(() -> {
                if (action != null) {
                    action.action(frm);
                }
            });
        });
    }
}
