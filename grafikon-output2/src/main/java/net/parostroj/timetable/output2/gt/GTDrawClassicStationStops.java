package net.parostroj.timetable.output2.gt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TimeIntervalResult.Status;
import net.parostroj.timetable.model.events.*;

import com.google.common.base.Predicate;

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
        nodeIntervalLists = new HashMap<Node, List<TimeIntervalList>>();
        locationMap = new HashMap<TimeInterval, Integer>();
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        inStationGap = (int) (TRAIN_STROKE_WIDTH * 1.75f * zoom);
        trainSsStroke = new BasicStroke(zoom * TRAIN_SS_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    @Override
    protected void paintTrainsInStation(Node station, Graphics2D g) {
        for (NodeTrack nodeTrack : station.getTracks()) {
            for (TimeInterval interval : nodeTrack.getTimeIntervalList()) {
                if (intervalFilter != null && !intervalFilter.apply(interval)) {
                    continue;
                }
                if (this.isPlacedInterval(interval)) {
                    if (interval.getLength() > 0) {
                        g.setStroke(getTrainStroke(interval.getTrain()));
                    } else {
                        g.setStroke(trainSsStroke);
                    }
                    g.setColor(this.getIntervalColor(interval));

                    this.paintTrainInStationWithInterval(g, interval);
                }
            }
        }
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
        if (this.isPlacedInterval(interval)) {
            Integer location = locationMap.get(interval);
            if (location == null) {
                List<TimeIntervalList> im = nodeIntervalLists.get(interval.getOwnerAsNode());
                if (im == null) {
                    im = new LinkedList<TimeIntervalList>();
                    nodeIntervalLists.put(interval.getOwnerAsNode(), im);
                }
                location = this.findLocation(interval, im);
                locationMap.put(interval, location);
            }
            y += this.convertLocationToShift(location);
        }
        return y;
    }

    private int convertLocationToShift(int location) {
        boolean even = (location & 1) == 0;
        int shift = 0;
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
            TimeIntervalList list = null;
            if (level >= im.size()) {
                list = new TimeIntervalList();
                im.add(list);
            } else {
                list = im.get(level);
            }
            TimeIntervalResult result = list.testIntervalForRouteSegment(interval);
            if (result.getStatus() == Status.OK) {
                list.addIntervalForRouteSegment(interval);
                found = true;
            } else {
                level++;
            }

        }
        return level;
    }

    @Override
    public Refresh processEvent(GTEvent<?> event) {
        Refresh refresh = super.processEvent(event);
        GTDrawEventVisitor visitor = new GTDrawEventVisitor() {
            @Override
            public void visit(TrainDiagramEvent event) {
                if (event.getType() == GTEventType.TRAIN_REMOVED) {
                    nodeIntervalLists.clear();
                    locationMap.clear();
                }
            }

            @Override
            public void visit(TrainEvent event) {
                if (event.getType() == GTEventType.TIME_INTERVAL_LIST || event.getType() == GTEventType.TECHNOLOGICAL) {
                    nodeIntervalLists.clear();
                    locationMap.clear();
                }
            }
        };
        event.accept(visitor);
        return refresh.update(visitor.getRefresh());
    }
}
