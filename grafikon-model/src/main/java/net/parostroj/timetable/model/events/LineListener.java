package net.parostroj.timetable.model.events;

/**
 * Line listener.
 * 
 * @author jub
 */
public interface LineListener extends GTListener {

    public void lineChanged(LineEvent event);
}
