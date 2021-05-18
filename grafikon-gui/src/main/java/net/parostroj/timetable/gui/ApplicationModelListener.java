/*
 * ApplicationModelListener.java
 *
 * Created on 2.9.2007, 15:14:26
 */

package net.parostroj.timetable.gui;

/**
 * Listener for application model events.
 *
 * @author jub
 */
public interface ApplicationModelListener {
    void modelChanged(ApplicationModelEvent event);
}
