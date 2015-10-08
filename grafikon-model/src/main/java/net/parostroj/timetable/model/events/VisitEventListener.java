package net.parostroj.timetable.model.events;

import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Listener for visiting events.
 *
 * @author jub
 */
public class VisitEventListener implements Listener {

    private final EventVisitor visitor;

    public VisitEventListener(EventVisitor visitor) {
        super();
        this.visitor = visitor;
    }

    @Override
    public void changed(Event event) {
        EventProcessing.visit(event, visitor);
    }
}
