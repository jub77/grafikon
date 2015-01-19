package net.parostroj.timetable.model.events;

/**
 * Trains cycle listener.
 * 
 * @author jub
 */
public interface TrainsCycleListener extends GTListener {

    public void trainsCycleChanged(TrainsCycleEvent event);
}
