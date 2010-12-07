package net.parostroj.timetable.gui;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

import javax.swing.RepaintManager;
import javax.swing.UIManager;

import net.parostroj.timetable.gui.ApplicationStarter.AfterStartAction;
import net.parostroj.timetable.gui.utils.CheckThreadViolationRepaintManager;

/**
 * Class with main method.
 *
 * @author jub
 */
public class Main {

    private static final Logger netParostrojLogger = Logger.getLogger("net.parostroj");

    static {
        netParostrojLogger.setLevel(Level.FINE);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);

        // add file output to logging
        try {
            File file = new File(System.getProperty("java.io.tmpdir"), "grafikon.log");
            Handler handler = new FileHandler(file.getCanonicalPath());
            handler.setFormatter(new SimpleFormatter());
            Logger.getLogger("").addHandler(handler);
        } catch (IOException e) {
            netParostrojLogger.log(Level.WARNING, "Cannot initialize logging file.", e);
        }
    }

    public static void main(final String[] args) throws Exception {
        if (AppPreferences.getPreferences().getBoolean("debug", false))
            setDebug();
        setLookAndFeel();
        ApplicationStarter<MainFrame> starter = new ApplicationStarter<MainFrame>(MainFrame.class, 290, 103, Main.class.getResource("/images/splashscreen.png"));
        starter.setAction(new AfterStartAction<MainFrame>() {

            @Override
            public void action(MainFrame frame) {
                if (args.length > 0) {
                    // trying to load file
                    File file = new File(args[0]);
                    if (file.exists()) {
                        netParostrojLogger.log(Level.FINE, "Loading: " + file.getName());
                        frame.forceLoad(file);
                    }
                }
            }
        });
        starter.start();
    }

    private static void setDebug() throws Exception {
        if (AppPreferences.getPreferences().getBoolean("debug.edt", false))
            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager(false));
        Level level = Level.parse(AppPreferences.getPreferences().getString("debug.level", "FINER"));
        netParostrojLogger.setLevel(level);
    }

    private static void setLookAndFeel() throws IOException {
        String laf = AppPreferences.getPreferences().getString("look.and.feel", "system");
        try {
            if (laf.equals("system"))
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            else
                UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            netParostrojLogger.log(Level.WARNING, "Error setting up look and feel.", e);
        }
    }
}
