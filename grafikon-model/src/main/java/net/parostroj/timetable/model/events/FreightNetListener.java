package net.parostroj.timetable.model.events;

/**
 * Net listener.
 *
 * @author jub
 */
public interface FreightNetListener extends GTListener {

    public void freightNetChanged(FreightNetEvent event);
}
