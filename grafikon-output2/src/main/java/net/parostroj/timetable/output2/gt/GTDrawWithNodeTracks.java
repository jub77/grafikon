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
    private static final float STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH = 1.0f;
    private static final float[] STATION_STROKE_STOP_EXT_DASH = { 3f, 3f };
    private static final float[] STATION_STROKE_STOP_WITH_FREIGHT_EXT_DASH = { 16f, 5f };

    private static final int TRACK_GAP_WIDTH = 5;

    private final TrainStrokeCache trainStrokeCache;
    private final Stroke trainSsStroke;
    private final Stroke stationStroke;
    private final Stroke techTimeStroke;
    private final Stroke stationStrokeRouteSplitExt;
    private final Stroke stationStrokeStopExt;
    private final Stroke stationStrokeStopWithFreightExt;

    private final int trackGap;

    private Map<Track,Integer> trackPositions;

    public GTDrawWithNodeTracks(GTDrawSettings config, Route route, TrainRegionCollector collector,
            Predicate<TimeInterval> intervalFilter, TrainColorChooser chooser, HighlightedTrains highlightedTrains) {
        super(config ,route, collector, intervalFilter, chooser, highlightedTrains);
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        trainStrokeCache = new TrainStrokeCache(TRAIN_STROKE_WIDTH, zoom, 10f);
        stationStroke = new BasicStroke(zoom * STATION_STROKE_WIDTH);
        trainSsStroke = new BasicStroke(zoom * TRAIN_SS_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        techTimeStroke = new BasicStroke(zoom * TECH_TIME_STROKE_WIDTH);
        stationStrokeRouteSplitExt = new BasicStroke(zoom * STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH);
        stationStrokeStopExt = new BasicStroke(zoom * STATION_STROKE_STOP_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, DrawUtils.zoomDashes(STATION_STROKE_STOP_EXT_DASH, zoom, 1.0f), 0f);
        stationStrokeStopWithFreightExt = new BasicStroke(zoom * STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, DrawUtils.zoomDashes(STATION_STROKE_STOP_WITH_FREIGHT_EXT_DASH, zoom, 1.0f), 0f);
        trackGap = (int) (zoom * TRACK_GAP_WIDTH);
    }

    @Override
    protected void computePositions() {
        positions = new HashMap<>();
        trackPositions = new HashMap<>();
        stations = new LinkedList<>();

        int completeLength = 0;
        int trackGaps = 0;
        for (RouteSegment<?> segment : route.getSegments()) {
            if (segment instanceof Line) {
                completeLength = completeLength + ((Line) segment).getLength();
            }
            if (segment instanceof Node) {
                trackGaps = trackGaps + segment.getTracks().size() - 1;
            }
        }

        double position = 0;
        int height = orientationDelegate.getStationsSize(size) - trackGaps * trackGap;
        double step = (double)height / (double)completeLength;
        for (RouteSegment<?> segment : route.getSegments()) {
            if (segment instanceof Line) {
                position = position + ((Line) segment).getLength() * step;
            }
            if (segment instanceof Node) {
                Node node = (Node) segment;
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
            if (config.get(GTDrawSettings.Key.EXTENDED_LINES) == Boolean.TRUE) {
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
                int sx = orientationDelegate.getHoursStart(start);
                int dx = orientationDelegate.getHoursSize(size);
                orientationDelegate.drawLine(g, sx, y, sx + dx, y);
            }
        }
    }

    @Override
    protected void paintTrainsInStation(Node station, Graphics2D g) {
        for (TimeInterval interval : station) {
            if (!checkIntervalFilter(interval)) {
                continue;
            }
            boolean technological = interval.isTechnological();
            if (technological) {
                if (interval.isJoiningTrains()) {
                    technological = false;
                } else if (!config.isOption(GTDrawSettings.Key.TECHNOLOGICAL_TIME)) {
                    continue;
                }
            }
            boolean boundary = interval.isBoundary();
            boolean showBoundary = config.isOption(GTDrawSettings.Key.TRAIN_ENDS);
            if (!boundary) {
                g.setStroke(getTrainStroke(interval.getTrain()));
            } else if (technological) {
                g.setStroke(techTimeStroke);
            } else {
                g.setStroke(trainSsStroke);
            }
            g.setColor(this.getIntervalColor(interval));

            if (boundary && showBoundary || interval.getLength() > 0) {
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
        return position != null ? orientationDelegate.getStationsStart(start) + position : -1;
    }

    @Override
    protected TrainStrokeCache getTrainStrokeCache() {
        return trainStrokeCache;
    }
}
