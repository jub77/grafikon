package net.parostroj.timetable.gui.components;

import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.ToolTipManager;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Graphical timetable view - with interaction.
 *
 * @author jub
 */
public class GraphicalTimetableView extends GraphicalTimetableViewDraw  {

    private static final Logger LOG = LoggerFactory.getLogger(GraphicalTimetableView.class);

    static {
        ToolTipManager.sharedInstance().setReshowDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    private interface ToolTipHelper {
        public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval);
    }

    private TextTemplate toolTipTemplateLine;
    private TextTemplate toolTipTemplateNode;
    private TimeInterval lastToolTipInterval;
    private final Map<String, Object> toolTipformattingMap = new HashMap<String, Object>();

    public GraphicalTimetableView() {
        toolTipformattingMap.put("helper", new ToolTipHelper() {

            @Override
            public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(TrainsCycleType.ENGINE_CYCLE, interval);
            }

            @Override
            public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(TrainsCycleType.TRAIN_UNIT_CYCLE, interval);
            }

            @Override
            public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(TrainsCycleType.DRIVER_CYCLE, interval);
            }
        });
        // tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
        try {
            toolTipTemplateLine = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.line"), TextTemplate.Language.GROOVY);
            toolTipTemplateNode = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.node"), TextTemplate.Language.GROOVY);
        } catch (GrafikonException e) {
            LOG.error("Error creating template for time interval.", e);
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (trainRegionCollector == null)
            return null;
        List<TimeInterval> intervals = trainRegionCollector.getItemsForPoint(event.getX(), event.getY());

        if (lastToolTipInterval == null) {
            if (!intervals.isEmpty())
                lastToolTipInterval = intervals.get(0);
        } else {
            lastToolTipInterval = null;
        }
        return lastToolTipInterval !=null ? this.formatTimeInterval() : null;
    }

    private String formatTimeInterval() {
        toolTipformattingMap.put("interval", lastToolTipInterval);
        if (lastToolTipInterval.isLineOwner())
            return toolTipTemplateLine.evaluate(toolTipformattingMap);
        else
            return toolTipTemplateNode.evaluate(toolTipformattingMap);
    }
}
