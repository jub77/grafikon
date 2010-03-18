package net.parostroj.timetable.gui;

import net.parostroj.timetable.mediator.AbstractColleague;

/**
 * Colleague for application model.
 *
 * @author jub
 */
public class ApplicationModelColleague extends AbstractColleague implements ApplicationModelListener {

    public ApplicationModelColleague(ApplicationModel model) {
        model.addListener(this);
    }

    @Override
    public void receiveMessage(Object message) {
        // do not react to messages
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        this.sendMessage(event);
    }
}
