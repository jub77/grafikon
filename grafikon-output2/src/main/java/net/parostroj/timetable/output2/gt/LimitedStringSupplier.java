package net.parostroj.timetable.output2.gt;

import java.awt.Graphics2D;

/**
 * Supplier of string with the length limited by width in pixels.
 *
 * @author jub
 */
public interface LimitedStringSupplier {

    /**
     * returns string with preffered width (it can be longer, if there is some minimum length).
     * If <code>0</code> is used the string is returned whole.
     *
     * @param g graphics
     * @param width desired width
     * @return string
     */
    String get(Graphics2D g, int width);
}
