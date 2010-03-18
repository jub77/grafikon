package net.parostroj.timetable.model.events;

/**
 * Train diagram listener with nested events.
 * 
 * @author jub
 */
public interface TrainDiagramListenerWithNested extends TrainDiagramListener {

    public void trainDiagramChangedNested(TrainDiagramEvent event);
}
