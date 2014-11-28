package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.util.*;

import com.google.common.base.Predicate;

import net.parostroj.timetable.model.*;

/**
 * Classic graphical timetable.
 *
 * @author jub
 */
public class GTDrawClassic extends GTDrawBase {

    // basic display
    protected static final float TRAIN_STROKE_WIDTH = 2.5f;
    protected static final float STATION_STROKE_WIDTH = 1.6f;

    // extended display
    private static final float STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH = 1.3f;
    private static final float STATION_STROKE_STOP_EXT_WIDTH = 1.3f;
    private static final float STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH = 1.3f;
    private static final float[] STATION_STROKE_STOP_EXT_DASH = { 3f, 3f };
    private static final float[] STATION_STROKE_STOP_WITH_FREIGHT_EXT_DASH = { 13f, 5f };
    private static final float[] TRAIN_STROKE_DASH = { 10f, 4f };
    private static final float[] TRAIN_STROKE_DASH_AND_DOT = { 10f, 3f, 1f, 3f };

    private final Stroke trainStroke;
    private final Stroke stationStroke;
    private final Stroke stationStrokeRouteSplitExt;
    private final Stroke stationStrokeStopExt;
    private final Stroke stationStrokeStopWithFreightExt;

    private final Map<LineType, Stroke> trainStrokes;

    public GTDrawClassic(GTDrawSettings config, Route route, TrainRegionCollector collector, Predicate<TimeInterval> intervalFilter) {
        super(config ,route, collector, intervalFilter);

        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        trainStroke = createTrainStroke(TRAIN_STROKE_WIDTH, null, zoom);
        stationStroke = new BasicStroke(zoom * STATION_STROKE_WIDTH);
        stationStrokeRouteSplitExt = new BasicStroke(zoom * STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH);
        stationStrokeStopExt = new BasicStroke(zoom * STATION_STROKE_STOP_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, zoomDashes(STATION_STROKE_STOP_EXT_DASH, zoom), 0f);
        stationStrokeStopWithFreightExt = new BasicStroke(zoom * STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, zoomDashes(STATION_STROKE_STOP_WITH_FREIGHT_EXT_DASH, zoom), 0f);

        Map<LineType, Stroke> lStrokes = new EnumMap<LineType, Stroke>(LineType.class);
        lStrokes.put(LineType.SOLID, trainStroke);
        lStrokes.put(LineType.DASH, createTrainStroke(TRAIN_STROKE_WIDTH, TRAIN_STROKE_DASH, zoom));
        lStrokes.put(LineType.DASH_AND_DOT, createTrainStroke(TRAIN_STROKE_WIDTH, TRAIN_STROKE_DASH_AND_DOT, zoom));
        trainStrokes = Collections.unmodifiableMap(lStrokes);
    }

    @Override
    protected void computePositions() {
        positions = new HashMap<Node, Integer>();
        stations = new LinkedList<Node>();

        int completeLength = 0;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asLine() != null) {
                completeLength = completeLength + segment.asLine().getLength();
            }
        }

        int incrementalLength = 0;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asLine() != null) {
                incrementalLength = incrementalLength + segment.asLine().getLength();
            }
            if (segment.asNode() != null) {
                stations.add(segment.asNode());
                positions.put(segment.asNode(), (int)(((double)incrementalLength) / completeLength *
                        orientationDelegate.getStationsSize(size)));
            }
        }
    }

    @Override
    protected void paintStations(Graphics2D g) {
        g.setStroke(stationStroke);
        g.setColor(Color.orange);
        for (Node s : stations) {
            // skip over signals
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
            int y = this.getY(s, null);
            int sx = orientationDelegate.getHoursStart(start);
            int dx = orientationDelegate.getHoursSize(size);
            orientationDelegate.drawLine(g, sx, y, sx + dx, y);
        }
    }

    @Override
    protected void paintTrainsInStation(Node asNode, Graphics2D g) {
        // nothing to paint
    }

    @Override
    protected Stroke getTrainStroke() {
        return trainStroke;
    }

    @Override
    protected Stroke getTrainStroke(LineType type) {
        return trainStrokes.get(type);
    }
}
