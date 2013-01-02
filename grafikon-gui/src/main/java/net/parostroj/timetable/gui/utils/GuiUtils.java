package net.parostroj.timetable.gui.utils;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * GUI utils class.
 *
 * @author jub
 */
public class GuiUtils {
    public static void setPosition(String preferences, Component component) {
        if (preferences == null)
            return;
        StringTokenizer tokenizer = new StringTokenizer(preferences, "|");
        List<Integer> values = new LinkedList<Integer>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            values.add(Integer.valueOf(token));
        }
        if (values.size() == 4) {
            Rectangle r = new Rectangle(values.get(0),
                    values.get(1),
                    values.get(2),
                    values.get(3));
            component.setBounds(r);
        }
    }

    public static String getPosition(Component component) {
        return String.format("%d|%d|%d|%d", component.getX(), component.getY(), component.getWidth(), component.getHeight());
    }

}
