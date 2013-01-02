package net.parostroj.timetable.mediator;

/**
 * Colleague implementation.
 *
 * @author jub
 */
public abstract class AbstractColleague implements ColleagueWithBackReference {

    private Mediator mediator;

    public AbstractColleague() {
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void sendMessage(Object message) {
        mediator.sendMessage(message);
    }

    @Override
    public Mediator getMediator() {
        return mediator;
    }

    public abstract void receiveMessage(Object message);
}
