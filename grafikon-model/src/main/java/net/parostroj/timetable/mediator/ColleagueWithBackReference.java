package net.parostroj.timetable.mediator;

/**
 * Colleague with a reference back to mediator.
 *
 * @author jub
 */
public interface ColleagueWithBackReference extends Colleague {

    void setMediator(Mediator mediator);

    Mediator getMediator();

    void sendMessage(Object message);
}
