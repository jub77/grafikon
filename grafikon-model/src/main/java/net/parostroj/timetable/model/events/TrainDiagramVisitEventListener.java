package net.parostroj.timetable.model.events;

import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Listener for visiting events. In case of nested event, the train diagram event is not
 * handled, instead the most inner event is handled.
 *
 * @author jub
 */
public class TrainDiagramVisitEventListener implements TrainDiagramListener {

    private final EventVisitor visitor;

    public TrainDiagramVisitEventListener(EventVisitor visitor) {
        super();
        this.visitor = visitor;
    }

    @Override
    public void trainDiagramChanged(TrainDiagramEvent event) {
        event.getLastNestedEvent().accept(visitor);
    }
}
