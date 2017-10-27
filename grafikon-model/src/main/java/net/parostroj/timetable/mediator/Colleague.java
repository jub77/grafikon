package net.parostroj.timetable.mediator;

/**
 * Colleague.
 *
 * @author jub
 */
@FunctionalInterface
public interface Colleague {

    public void receiveMessage(Object message);

}
