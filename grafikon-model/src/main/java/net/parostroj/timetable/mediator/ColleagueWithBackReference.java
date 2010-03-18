package net.parostroj.timetable.mediator;

/**
 * Colleague with a reference back to mediator.
 *
 * @author jub
 */
public interface ColleagueWithBackReference extends Colleague {

    public void setMediator(Mediator mediator);

    public Mediator getMediator();

    public void sendMessage(Object message);
}
