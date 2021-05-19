package net.parostroj.timetable.gui;

import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.net.URL;

import java.util.function.Consumer;
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

    @FunctionalInterface
    public interface StartAction<T> {

        void action(T frame);
    }

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);

    private final Class<T> applicationClass;
    private final Image image;
    private final Image iconImage;
    private final int x;
    private final int y;
    private final StartAction<T> afterAction;

    public ApplicationStarter(Class<T> applicationClass, int x, int y, URL url, URL iconUrl,
            StartAction<T> afterAction) {
        this.x = x;
        this.y =y;
        this.applicationClass = applicationClass;
        this.afterAction = afterAction;
        this.image = this.loadImage(url);
        this.iconImage = this.loadImage(iconUrl);
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
            return applicationClass.getConstructor(SplashScreenInfo.class, Image.class).newInstance(splash, iconImage);
        } catch (NoSuchMethodException e) {
            try {
                return applicationClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new ApplicationStarterException("Cannot create application (without splash)", e);
            }
        } catch (Exception ex) {
            throw new ApplicationStarterException("Cannot create application (with splash)", ex);
        }
    }

    private void startOriginal() throws ApplicationStarterException {
        log.info("Using Java 1.6 splash screen.");
        SplashScreen splash = SplashScreen.getSplashScreen();
        SplashScreenInfoOrig info = new SplashScreenInfoOrig(splash, x, y);
        final T frm = this.getApplicationInstance(info);
        GuiComponentUtils.runLaterInEDT(() -> {
            frm.setVisible(true);
            GuiComponentUtils.runLaterInEDT(() -> {
                if (afterAction != null) {
                    afterAction.action(frm);
                }
            });
        });
    }

    private void startFrame() throws ApplicationStarterException {
        log.info("Showing JFrame splash screen.");
        final SplashScreenFrame spl = new SplashScreenFrame(x, y, image);
        spl.setIconImage(iconImage);
        spl.setVisible(true);
        log.trace("Splash initialized.");
        final T frm = this.getApplicationInstance(spl);
        GuiComponentUtils.runLaterInEDT(() -> {
            spl.setVisible(false);
            spl.dispose();
            frm.setVisible(true);
            GuiComponentUtils.runLaterInEDT(() -> {
                if (afterAction != null) {
                    afterAction.action(frm);
                }
            });
        });
    }
}
