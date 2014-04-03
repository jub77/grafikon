package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.model.*;

/**
 * Classic graphical timetable.
 *
 * @author jub
 */
public class GTDrawClassic extends GTDraw {

    // basic display
    private static final float TRAIN_STROKE_WIDTH = 2.5f;
    private static final float STATION_STROKE_WIDTH = 1.6f;

    // extended display
    private static final float STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH = 1.3f;
    private static final float STATION_STROKE_STOP_EXT_WIDTH = 1.3f;
    private static final float SSSE_DASH_1 = 3f;
    private static final float SSSE_DASH_2 = 3f;
    private static final float STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH = 1.3f;
    private static final float SSSWFE_DASH_1 = 13f;
    private static final float SSSWFE_DASH_2 = 5f;

    private final Stroke trainStroke;
    private final Stroke stationStroke;
    private final Stroke stationStrokeRouteSplitExt;
    private final Stroke stationStrokeStopExt;
    private final Stroke stationStrokeStopWithFreightExt;

    public GTDrawClassic(GTViewSettings config, Route route, TrainRegionCollector collector, Filter<TimeInterval> intervalFilter) {
        super(config ,route, collector, intervalFilter);

        Float zoom = config.get(Key.ZOOM, Float.class);
        trainStroke = new BasicStroke(zoom * TRAIN_STROKE_WIDTH);
        stationStroke = new BasicStroke(zoom * STATION_STROKE_WIDTH);
        stationStrokeRouteSplitExt = new BasicStroke(zoom * STATION_STROKE_ROUTE_SPLIT_EXT_WIDTH);
        stationStrokeStopExt = new BasicStroke(zoom * STATION_STROKE_STOP_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { zoom * SSSE_DASH_1, zoom * SSSE_DASH_2 }, 0f);
        stationStrokeStopWithFreightExt = new BasicStroke(zoom * STATION_STROKE_STOP_WITH_FREIGHT_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { zoom * SSSWFE_DASH_1, zoom * SSSWFE_DASH_2 }, 0f);
    }

    @Override
    protected void computePositions() {
        positions = new HashMap<Node, Integer>();
        stations = new LinkedList<Node>();

        int completeLength = 0;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asLine() != null)
                completeLength = completeLength + segment.asLine().getLength();
        }

        int incrementalLength = 0;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asLine() != null)
                incrementalLength = incrementalLength + segment.asLine().getLength();
            if (segment.asNode() != null) {
                stations.add(segment.asNode());
                positions.put(segment.asNode(), (int)(((double)incrementalLength) / completeLength * size.height));
            }
        }
    }

    @Override
    protected void paintStations(Graphics2D g) {
        g.setStroke(stationStroke);
        g.setColor(Color.orange);
        for (Node s : stations) {
            // skip over signals
            if (s.getType() == NodeType.SIGNAL)
                continue;
            if (preferences.get(GTViewSettings.Key.EXTENDED_LINES) == Boolean.TRUE) {
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
            int y = start.y + positions.get(s);
            g.drawLine(start.x, y, start.x + size.width, y);
        }
    }

    @Override
    protected void paintTrains(Graphics2D g) {
        for (RouteSegment part : route.getSegments()) {
            // only segments for lines
            if (part.asLine() != null) {
                this.paintTrainsOnLine(part.asLine(), g, trainStroke);
            }
        }
    }

    @Override
    protected Line2D createTrainLine(TimeInterval interval, Interval i) {
        int x1 = this.getX(i.getStart());
        int x2 = this.getX(i.getEnd());
        int y1 = start.y + positions.get(interval.getFrom());
        int y2 = start.y + positions.get(interval.getTo());

        Line2D line2D = new Line2D.Double(x1, y1, x2, y2);
        return line2D;
    }
}
