package net.parostroj.timetable.model.events;

/**
 * All event listener.
 *
 * @author jub
 */
public interface AllEventListener extends GTListener {

    void changed(GTEvent<?> event);
}
