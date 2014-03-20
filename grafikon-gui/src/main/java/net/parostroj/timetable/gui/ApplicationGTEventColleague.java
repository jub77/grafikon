package net.parostroj.timetable.gui;

import net.parostroj.timetable.mediator.GTEventsReceiverColleague;

/**
 * Combined application and GT event colleague.
 *
 * @author jub
 */
public class ApplicationGTEventColleague extends GTEventsReceiverColleague {

    public ApplicationGTEventColleague() {
        super();
    }

    public ApplicationGTEventColleague(boolean theMostNested) {
        super(theMostNested);
    }

    @Override
    public void receiveMessage(Object message) {
        // process gt events
        super.receiveMessage(message);
        // process application events
        if (message instanceof ApplicationModelEvent) {
            processApplicationEvent((ApplicationModelEvent) message);
        }
    }

    public void processApplicationEvent(ApplicationModelEvent event) {}
}
