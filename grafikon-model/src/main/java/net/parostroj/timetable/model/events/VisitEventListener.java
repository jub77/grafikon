package net.parostroj.timetable.model.events;

import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Listener for visiting events.
 *
 * @author jub
 */
public class VisitEventListener implements AllEventListener {

    private final EventVisitor visitor;

    public VisitEventListener(EventVisitor visitor) {
        super();
        this.visitor = visitor;
    }

    @Override
    public void changed(GTEvent<?> event) {
        event.accept(visitor);
    }
}
