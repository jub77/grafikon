package net.parostroj.timetable.model.events;

/**
 * Train listener.
 * 
 * @author jub
 */
public interface TrainListener extends GTListener {

    public void trainChanged(TrainEvent event);
}
