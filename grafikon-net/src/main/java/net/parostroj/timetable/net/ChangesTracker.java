package net.parostroj.timetable.net;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.mediator.AbstractColleague;
import net.parostroj.timetable.model.events.GTEvent;

/**
 * This colleague tracks changes in the model in order to provides a list
 * of changed objects for network communication.
 *
 * @author jub
 */
public class ChangesTracker extends AbstractColleague {

    private List<DiagramChange> changes;
    private TrackedCheckVisitor trackedVisitor;
    private TransformVisitor transformVisitor;
    private Set<ChangesTrackerListener> listeners;

    public ChangesTracker() {
        changes = new LinkedList<DiagramChange>();
        trackedVisitor = new TrackedCheckVisitor();
        transformVisitor = new TransformVisitor();
        listeners = new HashSet<ChangesTrackerListener>();
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
        event.accept(transformVisitor);
        DiagramChange change = transformVisitor.getChange();
        changes.add(change);

        // inform listeners
        for (ChangesTrackerListener l : this.listeners) {
            l.changeReceived(change);
        }
    }

    private boolean isTracked(GTEvent<?> event) {
        event.accept(trackedVisitor);
        return trackedVisitor.isTracked();
    }

    public void addListener(ChangesTrackerListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ChangesTrackerListener listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }
}
