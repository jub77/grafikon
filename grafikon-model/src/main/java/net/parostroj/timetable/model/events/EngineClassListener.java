package net.parostroj.timetable.model.events;

/**
 * Engine class listener.
 * 
 * @author jub
 */
public interface EngineClassListener extends GTListener {

    public void engineClassChanged(EngineClassEvent event);
}
