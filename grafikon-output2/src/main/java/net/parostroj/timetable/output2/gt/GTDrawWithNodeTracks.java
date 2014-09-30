package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.util.*;

import com.google.common.base.Predicate;

import net.parostroj.timetable.model.*;

/**
 * Graphical timetable with node tracks.
 *
 * @author jub
 */
public class GTDrawWithNodeTracks extends GTDrawBase {

    // basic display
    private static final float TRAIN_STROKE_WIDTH = 1.5f;
    private static final float TRAIN_SS_STROKE_WIDTH = 5.0f;
    private static final float STATION_STROKE_WIDTH = 1.1f;
    private static final float TECH_TIME_STROKE_WIDTH = 1.1f;

    // extended display
    private static final float STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH = 0.8f;
    private static final float STATION_STROKE_STOP_EXT_WIDTH = 1.0f;
    private static final float SSSE_DASH_1 = 3f;
    private static final float SSSE_DASH_2 = 3f;
    private static final float STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH = 1.0f;
    private static final float SSSWFE_DASH_1 = 16f;
    private static final float SSSWFE_DASH_2 = 5f;

    private static final int TRACK_GAP_WIDTH = 5;

    private final Stroke trainStroke;
    private final Stroke trainSsStroke;
    private final Stroke stationStroke;
    private final Stroke techTimeStroke;
    private final Stroke stationStrokeRouteSplitExt;
    private final Stroke stationStrokeStopExt;
    private final Stroke stationStrokeStopWithFreightExt;

    private final int trackGap;

    private Map<Track,Integer> trackPositions;

    public GTDrawWithNodeTracks(GTDrawSettings config, Route route, TrainRegionCollector collector, Predicate<TimeInterval> intervalFilter) {
        super(config ,route, collector, intervalFilter);
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        trainStroke = new BasicStroke(zoom * TRAIN_STROKE_WIDTH);
        stationStroke = new BasicStroke(zoom * STATION_STROKE_WIDTH);
        trainSsStroke = new BasicStroke(zoom * TRAIN_SS_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        techTimeStroke = new BasicStroke(zoom * TECH_TIME_STROKE_WIDTH);
        stationStrokeRouteSplitExt = new BasicStroke(zoom * STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH);
        stationStrokeStopExt = new BasicStroke(zoom * STATION_STROKE_STOP_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { zoom * SSSE_DASH_1, zoom * SSSE_DASH_2 }, 0f);
        stationStrokeStopWithFreightExt = new BasicStroke(zoom * STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { zoom * SSSWFE_DASH_1, zoom * SSSWFE_DASH_2 }, 0f);
        trackGap = (int) (zoom * TRACK_GAP_WIDTH);
    }

    @Override
    protected void computePositions() {
        positions = new HashMap<Node, Integer>();
        trackPositions = new HashMap<Track, Integer>();
        stations = new LinkedList<Node>();

        int completeLength = 0;
        int trackGaps = 0;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asLine() != null) {
                completeLength = completeLength + segment.asLine().getLength();
            }
            if (segment.asNode() != null) {
                trackGaps = trackGaps + segment.asNode().getTracks().size() - 1;
            }
        }

        double position = 0;
        int height = size.height - trackGaps * trackGap;
        double step = (double)height / (double)completeLength;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asLine() != null) {
                position = position + segment.asLine().getLength() * step;
            }
            if (segment.asNode() != null) {
                Node node = segment.asNode();
                stations.add(node);
                int tracks = node.getTracks().size();
                positions.put(node, (int)position + (((tracks - 1) * trackGap) / 2));
                trackPositions.put(node.getTracks().get(0),(int)position);
                for (int i = 1; i < tracks; i++) {
                    position = position + trackGap;
                    trackPositions.put(node.getTracks().get(i), (int)position);
                }
            }
        }
    }

    @Override
    protected void paintStations(Graphics2D g) {
        g.setStroke(stationStroke);
        g.setColor(Color.orange);
        for (Node s : stations) {
            // skip over signals ...
            if (s.getType() == NodeType.SIGNAL) {
                continue;
            }
            if (preferences.get(GTDrawSettings.Key.EXTENDED_LINES) == Boolean.TRUE) {
                switch (s.getType()) {
                    case STOP:
                        g.setStroke(stationStrokeStopExt);
                        break;
                    case STOP_WITH_FREIGHT:
                        g.setStroke(stationStrokeStopWithFreightExt);
                        break;
                    case ROUTE_SPLIT:
                        g.setStroke(stationStrokeRouteSplitExt);
                        break;
                    default:
                        g.setStroke(stationStroke);
                        break;
                }
            }
            for (NodeTrack track : s.getTracks()) {
                int y = this.getY(s, track);
                g.drawLine(start.x, y, start.x + size.width, y);
            }
        }
    }

    @Override
    protected void paintTrainsInStation(Node station, Graphics2D g, Stroke trainStroke) {
        for (NodeTrack nodeTrack : station.getTracks()) {
            for (TimeInterval interval : nodeTrack.getTimeIntervalList()) {
                if (intervalFilter != null && !intervalFilter.apply(interval)) {
                    continue;
                }
                if (interval.isTechnological() && preferences.get(GTDrawSettings.Key.TECHNOLOGICAL_TIME) != Boolean.TRUE) {
                    continue;
                }
                if (!interval.isBoundary()) {
                    g.setStroke(trainStroke);
                } else if (interval.isTechnological()) {
                    g.setStroke(techTimeStroke);
                } else {
                    g.setStroke(trainSsStroke);
                }
                g.setColor(this.getIntervalColor(interval));

                this.paintTrainInStationWithInterval(g, interval);
            }
        }
    }

    @Override
    public int getY(Node node, Track track) {
        Integer position = null;
        if (track != null) {
            position = trackPositions.get(track);
        } else {
            position = positions.get(node);
        }
        return position != null ? start.y + position : -1;
    }

    @Override
    protected Stroke getTrainStroke() {
        return trainStroke;
    }
}
