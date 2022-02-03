package net.parostroj.timetable.gui;

import java.io.File;
import java.io.IOException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import javax.swing.*;

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

    private static final String DEBUG_SECTION = "debug";
    private static final String DEBUG_KEY = "debug";

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
        Instant start = Instant.now();
        if (Boolean.TRUE.equals(AppPreferences.getPreferences().getSection(DEBUG_SECTION).get(DEBUG_KEY, Boolean.class, false))) {
            setDebug();
        }
        printJavaInfo();
        setLookAndFeel();
        ApplicationStarter<MainFrame> starter = new ApplicationStarter<>(
                MainFrame.class, 292, 102, Main.class.getResource("/images/splashscreen.png"),
                Arrays.asList(
                        Main.class.getResource("/images/grafikon16.png"),
                        Main.class.getResource("/images/grafikon32.png")),
                frame -> {
                    log.debug("Started in {}ms", Duration.between(start, Instant.now()).toMillis());
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Duration duration = Duration.between(start, Instant.now());
            log.debug("Running time: {} minutes and {} seconds",
                    duration.toMinutes(),
                    duration.toSecondsPart());
        }));
    }

    private static void printJavaInfo() {
        log.info("Java version: {}", System.getProperty("java.version"));
        log.info("Java vendor: {}", System.getProperty("java.vendor"));
        log.info("Java runtime version: {}", System.getProperty("java.runtime.version"));
    }

    private static void setDebug() throws IOException {
        if (Boolean.TRUE.equals(AppPreferences.getPreferences().getSection(DEBUG_SECTION).get("debug.edt", Boolean.class, false))) {
            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager(false));
        }
        String levelName = AppPreferences.getPreferences().getSection(DEBUG_SECTION).get("debug.log4j.level", "DEBUG");
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
