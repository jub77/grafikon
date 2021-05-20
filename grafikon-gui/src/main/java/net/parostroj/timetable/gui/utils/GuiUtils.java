package net.parostroj.timetable.gui.utils;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI utils class.
 *
 * @author jub
 */
public final class GuiUtils {

    public static final Logger log = LoggerFactory.getLogger(GuiUtils.class);

    private static final int BORDER_FOR_POSITIONING = 50;

    private GuiUtils() {}

    public static void setPosition(String preferences, Component component) {
        if (preferences == null)
            return;
        StringTokenizer tokenizer = new StringTokenizer(preferences, "|");
        List<Integer> values = new LinkedList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            values.add(Integer.valueOf(token));
        }
        if (values.size() == 4) {
            checkAndModifyLocationOnScreen(values);
            Rectangle r = new Rectangle(values.get(0),
                    values.get(1),
                    values.get(2),
                    values.get(3));
            component.setBounds(r);
        } else if (values.size() == 2) {
            checkAndModifyLocationOnScreen(values);
            Rectangle bounds = component.getBounds();
            bounds.x = values.get(0);
            bounds.y = values.get(1);
            component.setBounds(bounds);
        }
    }

    public static String getPositionFrame(Component component, boolean maximized) {
        if (!maximized)
            return getPosition(component);
        else
            return String.format("%d|%d", component.getX(), component.getY());
    }

    public static String getPosition(Component component) {
        return String.format("%d|%d|%d|%d", component.getX(), component.getY(), component.getWidth(), component.getHeight());
    }

    private static void checkAndModifyLocationOnScreen(List<Integer> location) {
        int x = location.get(0);
        int y = location.get(1);
        boolean checked = false;
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = environment.getScreenDevices();
        for (GraphicsDevice device : devices) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            Rectangle shrunkBounds = new Rectangle(bounds);
            // shrink by constant
            shrunkBounds.width = shrunkBounds.width - BORDER_FOR_POSITIONING;
            shrunkBounds.height = shrunkBounds.height - BORDER_FOR_POSITIONING;
            if (shrunkBounds.contains(x, y)) {
                checked = true;
                break;
            }
        }
        if (!checked) {
            // put the location on screen 0
            Rectangle bounds = devices[0].getDefaultConfiguration().getBounds();
            location.set(0, bounds.x + BORDER_FOR_POSITIONING);
            location.set(1, bounds.y + BORDER_FOR_POSITIONING);
            log.debug("Fixed window location: [{},{}]", location.get(0), location.get(1));
        }
    }
}
