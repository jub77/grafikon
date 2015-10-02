package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.ObjectWithId;

/**
 * All event listener.
 *
 * @author jub
 */
public interface AllEventListener extends GTListener {

    void changed(GTEvent<? extends ObjectWithId> event);
}
