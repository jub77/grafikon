package net.parostroj.timetable.gui;

import net.parostroj.timetable.mediator.Colleague;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.events.Event;

/**
 * Colleague for application model.
 *
 * @author jub
 */
public class ApplicationModelColleague implements ApplicationModelListener, Colleague {

    private final ApplicationModel model;
    private final Mediator mediator;

    public ApplicationModelColleague(ApplicationModel model, Mediator mediator) {
        this.model = model;
        this.mediator = mediator;
        model.addListener(this);
    }

    @Override
    public void receiveMessage(Object message) {
        if (!model.isModelChanged() && message instanceof Event) {
            // all model changes causes model changed
            model.setModelChanged(true);
        }
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        mediator.sendMessage(event);
    }
}
