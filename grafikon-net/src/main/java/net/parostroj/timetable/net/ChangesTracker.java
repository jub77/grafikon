package net.parostroj.timetable.net;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.parostroj.timetable.mediator.AbstractColleague;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.events.GTEvent;

/**
 * This colleague tracks changes in the model in order to provides a list
 * of changed objects for network communication.
 *
 * @author jub
 */
public class ChangesTracker extends AbstractColleague {

    private Map<String, List<GTEvent<?>>> changes;
    private TrackedCheckVisitor trackedVisitor;

    public ChangesTracker() {
        changes = new HashMap<String, List<GTEvent<?>>>();
        trackedVisitor = new TrackedCheckVisitor();
    }

    @Override
    public void receiveMessage(Object message) {
        if (!(message instanceof GTEvent))
            throw new IllegalArgumentException("Cannot process message with class: " + message.getClass().getName());

        // cast event
        GTEvent<?> event = (GTEvent<?>)message;
        event = event.getLastNestedEvent();

        // check if the event belongs to tracked events
        if (!isTracked(event))
            return;

        // add to changes
        ObjectWithId source = (ObjectWithId)event.getSource();
        List<GTEvent<?>> list = changes.get(source.getId());
        if (list == null) {
            list = new LinkedList<GTEvent<?>>();
            changes.put(source.getId(), list);
        }
        list.add(event);
    }

    private boolean isTracked(GTEvent<?> event) {
        trackedVisitor.clear();
        event.accept(trackedVisitor);
        return trackedVisitor.isTracked();
    }
}
