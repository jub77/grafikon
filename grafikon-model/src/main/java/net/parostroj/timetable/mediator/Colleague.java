package net.parostroj.timetable.mediator;

/**
 * Colleague.
 *
 * @author jub
 */
@FunctionalInterface
public interface Colleague {

    void receiveMessage(Object message);

}
