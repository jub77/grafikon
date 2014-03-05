package net.parostroj.timetable.model.events;

/**
 * Train diagram listener.
 * 
 * @author jub
 */
public interface TrainDiagramListener extends GTListener {

    public void trainDiagramChanged(TrainDiagramEvent event);
}
