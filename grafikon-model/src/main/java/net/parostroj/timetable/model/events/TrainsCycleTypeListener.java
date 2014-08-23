package net.parostroj.timetable.model.events;

/**
 * Trains cycle type listener.
 *
 * @author jub
 */
public interface TrainsCycleTypeListener extends GTListener {

    public void trainsCycleTypeChanged(TrainsCycleTypeEvent event);
}
