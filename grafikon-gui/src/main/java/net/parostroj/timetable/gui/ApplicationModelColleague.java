package net.parostroj.timetable.gui;

import net.parostroj.timetable.mediator.AbstractColleague;
import net.parostroj.timetable.model.events.*;

/**
 * Colleague for application model.
 *
 * @author jub
 */
public class ApplicationModelColleague extends AbstractColleague implements ApplicationModelListener {

    private final ApplicationModel model;

    public ApplicationModelColleague(ApplicationModel model) {
        this.model = model;
        model.addListener(this);
    }

    @Override
    public void receiveMessage(Object message) {
        if (!model.isModelChanged()) {
            if (message instanceof GTEvent<?>) {
                // all model changes causes model changed
                model.setModelChanged(true);
            }
        }
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        this.sendMessage(event);
    }
}
