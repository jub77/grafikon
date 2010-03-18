package net.parostroj.timetable.model;

import java.util.logging.Logger;
import net.parostroj.timetable.model.events.NetEvent;
import net.parostroj.timetable.model.events.NetListener;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.model.events.TrainListener;
import net.parostroj.timetable.model.events.TrainTypeEvent;
import net.parostroj.timetable.model.events.TrainTypeListener;
import net.parostroj.timetable.model.events.TrainsCycleEvent;
import net.parostroj.timetable.model.events.TrainsCycleListener;

/**
 * Listener implementation for train diagram.
 * 
 * @author jub
 */
class GTListenerTrainDiagramImpl implements TrainListener, TrainsCycleListener, NetListener, TrainTypeListener {

    private static final Logger LOG = Logger.getLogger(GTListenerTrainDiagramImpl.class.getName());
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
}
