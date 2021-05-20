package net.parostroj.timetable.output2.gt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.*;

import java.util.function.Predicate;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TimeIntervalResult.Status;
import net.parostroj.timetable.model.events.*;

/**
 * Modified classic look with trains in stations.
 *
 * @author jub
 */
public class GTDrawClassicStationStops extends GTDrawClassic {

    private static final float TRAIN_SS_STROKE_WIDTH = 1.3f * TRAIN_STROKE_WIDTH;

    private final Stroke trainSsStroke;

    private final Map<Node, List<TimeIntervalList>> nodeIntervalLists;
    private final Map<TimeInterval, Integer> locationMap;
    private final int inStationGap;

    public GTDrawClassicStationStops(GTDrawSettings config, Route route, TrainRegionCollector collector,
            Predicate<TimeInterval> intervalFilter, TrainColorChooser chooser, HighlightedTrains highlightedTrains) {
        super(config, route, collector, intervalFilter, chooser, highlightedTrains);
        nodeIntervalLists = new HashMap<>();
        locationMap = new HashMap<>();
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        inStationGap = (int) (TRAIN_STROKE_WIDTH * 1.75f * zoom);
        trainSsStroke = new BasicStroke(zoom * TRAIN_SS_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    @Override
    protected void paintTrainsInStation(Node station, Graphics2D g) {
        for (TimeInterval interval : station) {
            if (interval.isJoiningTrains() && checkIntervalFilter(interval)) {
                paintTrainInStationImpl(g, interval);
            }
         }
        for (TimeInterval interval : station) {
            if (checkIntervalFilter(interval) && isPlacedInterval(interval)) {
                paintTrainInStationImpl(g, interval);
            }
        }
    }

    private void paintTrainInStationImpl(Graphics2D g, TimeInterval interval) {
        if (interval.getLength() > 0) {
            g.setStroke(getTrainStroke(interval.getTrain()));
        } else {
            g.setStroke(trainSsStroke);
        }
        g.setColor(this.getIntervalColor(interval));

        this.paintTrainInStationWithInterval(g, interval);
    }

    private boolean isPlacedInterval(TimeInterval interval) {
        boolean showBoundary = config.getOption(GTDrawSettings.Key.TRAIN_ENDS);
        boolean boundary = interval.isBoundary();
        boolean inner = !interval.isTechnological() && interval.isInnerStop();
        return inner || boundary && showBoundary;
    }

    @Override
    public int getY(TimeInterval interval) {
        int y = this.getY(interval.getOwnerAsNode(), interval.getTrack());
        Integer location = locationMap.computeIfAbsent(interval, i -> {
            List<TimeIntervalList> im = nodeIntervalLists.computeIfAbsent(
                    interval.getOwnerAsNode(),
                    key -> new LinkedList<>());
            return this.findLocation(interval, im);

        });
        y += this.convertLocationToShift(location);
        return y;
    }

    private int convertLocationToShift(int location) {
        boolean even = (location & 1) == 0;
        int shift;
        if (even) {
            shift = -location * inStationGap / 2;
        } else {
            shift = (location + 1) * inStationGap / 2;
        }
        return shift;
    }

    private int findLocation(TimeInterval interval, List<TimeIntervalList> im) {
        int level = 0;
        boolean found = false;
        while (!found) {
            TimeIntervalList list;
            if (level >= im.size()) {
                list = new TimeIntervalList();
                im.add(list);
            } else {
                list = im.get(level);
            }
            TimeIntervalResult result = list.testIntervalForRouteSegment(interval);
            if (result.getStatus() == Status.OK) {
                list.addIntervalForRouteSegmentWithoutCheck(interval);
                found = true;
            } else {
                level++;
            }

        }
        return level;
    }

    @Override
    public Refresh processEvent(Event event) {
        Refresh refresh = super.processEvent(event);
        GTDrawEventVisitor visitor = new GTDrawEventVisitor() {
            @Override
            public void visitDiagramEvent(Event event) {
                if (event.getType() == Event.Type.REMOVED && event.getObject() instanceof Train) {
                    nodeIntervalLists.clear();
                    locationMap.clear();
                }
            }

            @Override
            public void visitTrainEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE && event.getAttributeChange()
                        .checkName(Train.ATTR_TECHNOLOGICAL_BEFORE, Train.ATTR_TECHNOLOGICAL_AFTER)
                        || event.getType() == Event.Type.SPECIAL) {
                    nodeIntervalLists.clear();
                    locationMap.clear();
                }
            }
        };
        EventProcessing.visit(event, visitor);
        return refresh.update(visitor.getRefresh());
    }
}
