package net.parostroj.timetable.model.events;

/**
 * Net listener.
 * 
 * @author jub
 */
public interface NetListener extends GTListener {

    public void netChanged(NetEvent event);
}
