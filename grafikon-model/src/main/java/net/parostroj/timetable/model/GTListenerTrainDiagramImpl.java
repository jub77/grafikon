package net.parostroj.timetable.model;

import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.events.*;

/**
 * Listener implementation for train diagram.
 *
 * @author jub
 */
class GTListenerTrainDiagramImpl implements TrainListener, TrainsCycleListener, AllEventListener, TrainTypeListener,
        TextItemListener, EngineClassListener, OutputTemplateListener {

    private final TrainDiagram diagram;

    protected GTListenerTrainDiagramImpl(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public void trainChanged(TrainEvent event) {
        diagram.fireNestedEvent(event);
        // keep circulations sorted
        if (event.getType() == GTEventType.TIME_INTERVAL_LIST) {
            Map<String, List<TrainsCycleItem>> map = event.getSource().getCyclesMap();
            for (List<TrainsCycleItem> iList : map.values()) {
                for (TrainsCycleItem item : iList) {
                    item.getCycle().correctItem(item);
                }
            }
        }
    }

    @Override
    public void trainsCycleChanged(TrainsCycleEvent event) {
        diagram.fireNestedEvent(event);
    }

    @Override
    public void changed(GTEvent<?> event) {
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

    @Override
    public void outputTemplateChanged(OutputTemplateEvent event) {
        diagram.fireNestedEvent(event);
    }
}
