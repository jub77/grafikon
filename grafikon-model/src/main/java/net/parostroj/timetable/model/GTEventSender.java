package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;

/**
 * Interface for sending events to proper listeners methods.
 *
 * @author jub
 */
abstract class GTEventSender<T extends GTListener, E extends GTEvent> {

    abstract public void fireEvent(T listener, E event);
}
