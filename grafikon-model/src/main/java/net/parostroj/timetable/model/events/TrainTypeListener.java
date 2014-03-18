package net.parostroj.timetable.model.events;

/**
 * TrainType listener.
 * 
 * @author jub
 */
public interface TrainTypeListener extends GTListener {

    public void trainTypeChanged(TrainTypeEvent event);
}
