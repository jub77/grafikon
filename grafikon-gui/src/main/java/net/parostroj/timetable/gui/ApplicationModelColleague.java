package net.parostroj.timetable.gui;

import net.parostroj.timetable.mediator.AbstractColleague;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TextItemEvent;
import net.parostroj.timetable.model.events.TrainDiagramEvent;

/**
 * Colleague for application model.
 *
 * @author jub
 */
public class ApplicationModelColleague extends AbstractColleague implements ApplicationModelListener {

    private ApplicationModel model;

    public ApplicationModelColleague(ApplicationModel model) {
        this.model = model;
        model.addListener(this);
    }

    @Override
    public void receiveMessage(Object message) {
        // react to some events to set changed model
        if (message instanceof TrainDiagramEvent) {
            TrainDiagramEvent tde = (TrainDiagramEvent)message;
            if (tde.getType() == GTEventType.TEXT_ITEM_ADDED || tde.getType() == GTEventType.TEXT_ITEM_REMOVED ||
                    tde.getType() == GTEventType.TEXT_ITEM_MOVED)
                model.setModelChanged(true);
            if (tde.getType() == GTEventType.NESTED && tde.getLastNestedEvent() instanceof TextItemEvent)
                model.setModelChanged(true);
        }
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        this.sendMessage(event);
    }
}
