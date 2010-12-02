package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;

/**
 * Listener implementation for train diagram.
 * 
 * @author jub
 */
class GTListenerTrainDiagramImpl implements TrainListener, TrainsCycleListener,
        NetListener, TrainTypeListener, TextItemListener, EngineClassListener {

    private TrainDiagram diagram;

    protected GTListenerTrainDiagramImpl(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public void trainChanged(TrainEvent event) {
        diagram.fireNestedEvent(event);
    }

    @Override
    public void trainsCycleChanged(TrainsCycleEvent event) {
        diagram.fireNestedEvent(event);
    }

    @Override
    public void netChanged(NetEvent event) {
        diagram.fireNestedEvent(event);
    }

    @Override
    public void trainTypeChanged(TrainTypeEvent event) {
        diagram.fireNestedEvent(event);
    }

    @Override
    public void textItemChanged(TextItemEvent event) {
        diagram.fireNestedEvent(event);
    }

    @Override
    public void engineClassChanged(EngineClassEvent event) {
        diagram.fireNestedEvent(event);
    }
}
