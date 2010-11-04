package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.Interval;

/**
 * Classic graphical timetable.
 *
 * @author jub
 */
public class GTDrawClassic extends GTDraw {

    // basic display
    private static final Stroke TRAIN_STROKE = new BasicStroke(2.5f);
    private static final Stroke STATION_STROKE = new BasicStroke(1.6f);
    
    // extended display
    private static final Stroke STATION_STROKE_STOP_EXT = new BasicStroke(1.3f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1.0f,new float[]{3f,3f},0f);
    private static final Stroke STATION_STROKE_STOP_WITH_FREIGHT_EXT = new BasicStroke(1.3f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1.0f,new float[]{16f,5f},0f);
    private static final Stroke STATION_STROKE_ROUTE_SPLIT_EXT = new BasicStroke(1.3f);
    

    public GTDrawClassic(GTViewSettings config, Route route, TrainRegionCollector collector) {
        super(config ,route, collector);
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
        g.setStroke(STATION_STROKE);
        g.setColor(Color.orange);
        for (Node s : stations) {
            // skip over signals
            if (s.getType() == NodeType.SIGNAL)
                continue;
            if (preferences.get(GTViewSettings.Key.EXTENDED_LINES) == Boolean.TRUE) {
                switch (s.getType()) {
                    case STOP:
                        g.setStroke(STATION_STROKE_STOP_EXT);
                        break;
                    case STOP_WITH_FREIGHT:
                        g.setStroke(STATION_STROKE_STOP_WITH_FREIGHT_EXT);
                        break;
                    case ROUTE_SPLIT:
                        g.setStroke(STATION_STROKE_ROUTE_SPLIT_EXT);
                        break;
                    default:
                        g.setStroke(STATION_STROKE);
                        break;
                }
            }
            int y = start.y + positions.get(s);
            g.drawLine(start.x, y, start.x + size.width, y);
        }
    }

    @Override
    protected void paintTrains(Graphics2D g) {
        double timeStep = (double)size.width / (24 * 3600);
        
        for (RouteSegment part : route.getSegments()) {
            // only segments for lines
            if (part.asLine() != null) {
                this.paintTrainsOnLine(part.asLine(), g, timeStep, TRAIN_STROKE);
            }
        }
    }

    @Override
    protected Line2D createTrainLine(TimeInterval interval, Interval i, double timeStep) {
        int x1 = (int)(start.x + i.getStart() * timeStep);
        int x2 = (int)(start.x + i.getEnd() * timeStep);
        int y1 = start.y + positions.get(interval.getFrom());
        int y2 = start.y + positions.get(interval.getTo());

        Line2D line2D = new Line2D.Double(x1, y1, x2, y2);
        return line2D;
    }
}
