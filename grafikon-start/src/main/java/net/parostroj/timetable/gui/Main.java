package net.parostroj.timetable.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.RepaintManager;
import javax.swing.UIManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ini.AppPreferences;
import net.parostroj.timetable.gui.utils.CheckThreadViolationRepaintManager;

/**
 * Class with main method.
 *
 * @author jub
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String LOGGING_PACKAGE = "net.parostroj";

    public static void setLoggingLevel(org.slf4j.event.Level level) {
        setLoggingLevelImpl(LOGGING_PACKAGE, Level.toLevel(level.toString(), Level.DEBUG));
    }

    public static void setLoggingLevel(String pkg, org.slf4j.event.Level level) {
        setLoggingLevelImpl(pkg, Level.toLevel(level.toString(), Level.DEBUG));
    }

    private static void setLoggingLevelImpl(String pkg, Level level) {
        Configurator.setLevel(pkg, level);
    }

    public static void main(final String[] args) throws Exception {
        if (AppPreferences.getPreferences().getSection("debug").get("debug", Boolean.class, false)) {
            setDebug();
        }
        printJavaInfo();
        setLookAndFeel();
        ApplicationStarter<MainFrame> starter = new ApplicationStarter<>(
                MainFrame.class, 292, 102, Main.class.getResource("/images/splashscreen.png"));
        starter.setAction(frame -> {
            if (args.length > 0) {
                // trying to load file
                File file = new File(args[0]);
                if (file.exists()) {
                    log.info("Loading: {}", file.getName());
                    frame.forceLoad(file);
                } else {
                    log.warn("File {} doesn't exist", file.getPath());
                }
            }
        });
        starter.start();
    }

    private static void printJavaInfo() {
        log.info("Java version: {}", System.getProperty("java.version"));
        log.info("Java vendor: {}", System.getProperty("java.vendor"));
        log.info("Java runtime version: {}", System.getProperty("java.runtime.version"));
    }

    private static void setDebug() throws IOException {
        if (AppPreferences.getPreferences().getSection("debug").get("debug.edt", Boolean.class, false)) {
            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager(false));
        }
        String levelName = AppPreferences.getPreferences().getSection("debug").get("debug.log4j.level", "DEBUG");
        Level level = Level.toLevel(levelName, Level.DEBUG);
        setLoggingLevelImpl(LOGGING_PACKAGE, level);
    }

    private static void setLookAndFeel() throws IOException {
        String laf = AppPreferences.getPreferences().getSection("main").get("look.and.feel", "system");
        try {
            if (laf.equals("system")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(laf);
            }
        } catch (Exception e) {
            log.warn("Error setting up look and feel.", e);
        }
    }
}
