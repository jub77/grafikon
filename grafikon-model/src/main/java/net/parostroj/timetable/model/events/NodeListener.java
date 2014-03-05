package net.parostroj.timetable.model.events;

/**
 * Node listener.
 * 
 * @author jub
 */
public interface NodeListener extends GTListener {

    public void nodeChanged(NodeEvent event);
}
