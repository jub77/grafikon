package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;

/**
 * Interface for sending events to proper listener methods.
 *
 * @author jub
 */
interface GTEventSender<T extends GTListener, E extends GTEvent<?>> {

    void fireEvent(T listener, E event);
}
