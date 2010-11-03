package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.geom.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.parostroj.timetable.gui.components.GraphicalTimetableView.TrainColors;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.TransformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for all graphical timetable draws.
 * 
 * @author jub
 */
abstract public class GTDraw {
    private static final Logger LOG = LoggerFactory.getLogger(GTDraw.class.getName());
    
    // basic display
    private static final Stroke HOURS_STROKE = new BasicStroke(1.8f);
    private static final Stroke HALF_HOURS_STROKE = new BasicStroke(.9f);
    private static final Stroke TEN_MINUTES_STROKE = new BasicStroke(0.4f);
    
    // extended display
    private static final Stroke HALF_HOURS_STROKE_EXT = new BasicStroke(1.1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1.0f,new float[]{15f,7f},0f);

    private static final int MINIMAL_SPACE = 25;

    protected Point start;
    
    protected Dimension size;
    
    protected int gapStationX;

    protected int borderX;

    protected int borderY;
    
    protected Route route;

    protected int positionX = 0;
    
    protected HighlightedTrains hTrains;
    
    private GraphicalTimetableView.TrainColors colors;
    
    private TrainColorChooser trainColorChooser;
    
    private TrainRegionCollector trainRegionCollector;
    
    protected Map<GTDrawPreference,Boolean> preferences;

    protected Map<Node,Integer> positions;

    protected List<Node> stations;

    protected Color background = Color.white;

    public GTDraw(int borderX, int borderY, int gapStationX, Dimension size, Route route, GraphicalTimetableView.TrainColors colors, TrainColorChooser chooser, HighlightedTrains hTrains, TrainRegionCollector collector) {
        this.gapStationX = gapStationX;
        this.borderX = borderX;
        this.borderY = borderY;
        this.route = route;
        this.colors = colors;
        this.trainColorChooser = chooser;
        this.hTrains = hTrains;
        this.trainRegionCollector = collector;
        
        // update start
        this.updateStart();
        
        // compute size
        this.setSize(size);
        
        // create preferences
        preferences = new EnumMap<GTDrawPreference, Boolean>(GTDrawPreference.class);
    }
    
    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

      
        if (positions == null) {
            this.computePositions();
        }
        
        this.paintHours(g);

        this.paintStations(g);
        
        g.setColor(Color.BLACK);
        
        this.paintTrains(g);

        this.paintStationNames(g, stations, positions);
        
        this.finishCollecting();
    }
    
    protected abstract void computePositions();
    
    protected abstract void paintStations(Graphics2D g);
    
    protected abstract void paintTrains(Graphics2D g);

    public void setPreference(GTDrawPreference key, boolean value) {
        preferences.put(key, Boolean.valueOf(value));
    }

    public boolean getPreference(GTDrawPreference key) {
        Boolean value = preferences.get(key);
        return Boolean.TRUE.equals(value);
    }

    public void setGapStationX(int gapStationX) {
        this.gapStationX = gapStationX;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setSize(Dimension size) {
        this.size = new Dimension(size.width - (borderX * 2 + gapStationX), size.height - borderY * 2);
        // clear stations and positions of nodes
        this.positions = null;
        this.stations = null;
    }

    public void setBorderX(int borderX) {
        this.borderX = borderX;
    }

    public void setBorderY(int borderY) {
        this.borderY = borderY;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    private void updateStart() {
        this.start = new Point(borderX, borderY);
        this.start.translate(gapStationX, 0);
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public Route getRoute() {
        return route;
    }

    public void setHTrains(HighlightedTrains hTrains) {
        this.hTrains = hTrains;
    }

    public void setTrainColors(TrainColors colors, TrainColorChooser trainColorChooser) {
        this.colors = colors;
        this.trainColorChooser = trainColorChooser;
    }

    protected void paintHours(Graphics2D g) {
        int yEnd = start.y + size.height;
        
        double step = ((double)size.width) / 24 / 2;
        
        double xCurr = start.x;

        // draw hours
        for (int i = 0; i <= 48; i++) {
            
            if ((i & 1) == 0) {
                // hours
                g.setColor(Color.orange);
                g.setStroke(HOURS_STROKE);
            } else {
                // half hours
                g.setColor(Color.orange);
                if (preferences.get(GTDrawPreference.EXTENDED_LINES) == Boolean.TRUE)
                    g.setStroke(HALF_HOURS_STROKE_EXT);
                else
                    g.setStroke(HALF_HOURS_STROKE);
            }
            
            if (((i & 1) != 1) || step >= MINIMAL_SPACE)
                // draw line
                g.drawLine((int)xCurr, start.y, (int)xCurr, yEnd);

            if ((i & 1) == 0) {
                // draw hours
                g.setColor(Color.black);
                g.drawString(Integer.toString(i / 2), (int)xCurr - 5, start.y - 3);
            }

            xCurr = xCurr + step;
        }
        
        // 10 minutes
        double tenMinutesStep = ((double)size.width) / (24 * 6);
        if (tenMinutesStep >= MINIMAL_SPACE) {
            xCurr = start.x;
            g.setColor(Color.orange);
            g.setStroke(TEN_MINUTES_STROKE);
            for (int i = 0; i <= 24*6; i++) {
                if (i % 3 !=0) {
                    // draw line
                    g.drawLine((int)xCurr, start.y, (int)xCurr, yEnd);
                }
                xCurr = xCurr + tenMinutesStep;
            }
        }
    }
    
    abstract protected Line2D createTrainLine(TimeInterval interval, Interval i, double timeStep);
    
    protected void paintTrainsOnLine(Line line, Graphics2D g, double timeStep, Stroke trainStroke) {
        g.setStroke(trainStroke);
        for (LineTrack track : line.getTracks()) {
            for (TimeInterval interval : track.getTimeIntervalList()) {
                boolean paintTrainName = (interval.getFrom().getType() != NodeType.SIGNAL) &&
                        (preferences.get(GTDrawPreference.TRAIN_NAMES) == Boolean.TRUE);
                boolean paintMinutes = preferences.get(GTDrawPreference.ARRIVAL_DEPARTURE_DIGITS) == Boolean.TRUE;

                Interval normalized = interval.getInterval().normalize();
                g.setColor(this.getIntervalColor(interval));
                this.paintTrainOnLineWithInterval(g, paintTrainName, paintMinutes, interval, normalized, timeStep);
                Interval overMidnight = normalized.getNonNormalizedIntervalOverMidnight();
                if (overMidnight != null) {
                    this.paintTrainOnLineWithInterval(g, paintTrainName, paintMinutes, interval, overMidnight, timeStep);
                }
            }
        }
        g.setColor(Color.BLACK);
    }

    private void paintTrainOnLineWithInterval(Graphics2D g, boolean paintTrainName, boolean paintMinutes, TimeInterval interval, Interval i, double timeStep) {
        Line2D line2D = this.createTrainLine(interval, i, timeStep);

        // add shape to collector
        if (this.isCollectorCollecting(interval.getTrain()))
            this.addShapeToCollector(interval, line2D);

        g.draw(line2D);

        if (paintTrainName)
            this.paintTrainNameOnLine(g, interval, line2D);
        if (paintMinutes)
            this.paintMinutesOnLine(g, interval, line2D);

    }

    protected void paintStationNames(Graphics2D g, List<Node> stations, Map<Node, Integer> positions) {
        for (Node s : stations) {
            // ignore signals
            if (s.getType() == NodeType.SIGNAL)
                continue;
            String name = TransformUtil.transformStation(s, null, null);
            int y = start.y + positions.get(s);
            // draw name of the station
            Font f = g.getFont();
            Rectangle2D b = null;
            String transName = name;
            int nameLength = name.length();
            while (b == null || b.getWidth() >= (gapStationX - 5)) {
                b = f.getStringBounds(transName, g.getFontRenderContext());
                if (b.getWidth() >= (gapStationX - 5)) {
                    nameLength -= 1;
                    transName = name.substring(0, nameLength);
                    transName += "...";
                }
            }
            Rectangle r = new Rectangle((int)b.getX() - 2, (int)b.getY(), (int)b.getWidth() + 4, (int)b.getHeight());
            r.setLocation(10 + 0 + positionX - 3, y + 12 + (int)r.getY());
            g.setColor(background);
            g.fill(r);
            g.setColor(Color.black);
            g.drawString(transName, 10 + 0 + positionX, y + 12);
        }
    }

    protected void paintTrainNameOnLine(Graphics2D g, TimeInterval interval, Line2D line) {
        // draw train name
        AffineTransform old = g.getTransform();
        double lengthY = line.getY2()-line.getY1();
        double lengthX = line.getX2()-line.getX1();
        double angle = Math.atan(lengthY / lengthX);
        // get length
        double length = Math.sqrt(Math.pow(lengthY, 2)+Math.pow(lengthX, 2));
        AffineTransform newTransform = g.getTransform();
        newTransform.translate(line.getX1(),line.getY1());
        newTransform.rotate(angle);
        g.setTransform(newTransform);
        // length of the text
        String text = interval.getTrain().getName();
        Rectangle2D rr = g.getFont().getStringBounds(text, g.getFontRenderContext());
        Shape nameShape = null;
        
        int shift = (int)(length - rr.getWidth()) / 2; 
        if (shift >= 0) {
            g.drawString(text, shift, -5);
            if (this.isCollectorCollecting(interval.getTrain())) {
                Rectangle rec = rr.getBounds();
                rec.translate(shift, -5);
                nameShape = newTransform.createTransformedShape(rec);
                try {
                    nameShape = old.createInverse().createTransformedShape(nameShape);
                } catch (Exception e) {
                    LOG.error("Error transform name shape.", e);
                }
            }
        }
        g.setTransform(old);
        
        if (nameShape != null) {
            this.addShapeToCollector(interval, nameShape);
        }
    }
    
    private Rectangle2D digitSize;
    
    private Rectangle2D getDigitSize(Graphics2D g) {
        if (digitSize == null) {
            digitSize = g.getFont().getStringBounds("0", g.getFontRenderContext());
        }
        return digitSize;
    }
    
    protected void paintMinutesOnLine(Graphics2D g, TimeInterval interval, Line2D line) {
        // check if I should draw end time
        boolean endTime = true;
        if (!interval.getNextTrainInterval().isStop()) {
            Train train = interval.getTrain();
            int ind = train.getTimeIntervalList().indexOf(interval);
            if ((ind + 2) < train.getTimeIntervalList().size()) {
                TimeInterval nextLineInterval = train.getTimeIntervalList().get(ind + 2);
                Node endNode2 = nextLineInterval.getTo();
                if (stations.contains(endNode2))
                    endTime = false;
            }
        }
        
        boolean downDirection = line.getY1() < line.getY2();
        Point2D startP = line.getP1();
        Point2D endP = line.getP2();
        Rectangle2D dSize = this.getDigitSize(g);
        if (downDirection) {
            startP.setLocation(startP.getX() + dSize.getWidth(), startP.getY() + dSize.getHeight() - 2);
            endP.setLocation(endP.getX() - 1.5 * dSize.getWidth(), endP.getY() - 2);
        } else {
            startP.setLocation(startP.getX() + dSize.getWidth(), startP.getY() - 2);
            endP.setLocation(endP.getX() - 1.5 * dSize.getWidth(), endP.getY() + dSize.getHeight() - 2);
        }
        g.setColor(Color.BLACK);
        if (interval.getFrom().getType() != NodeType.SIGNAL)
            g.drawString(TimeConverter.getLastDigitOfMinutes(interval.getStart()), (int)startP.getX(), (int)startP.getY());
        if (interval.getTo().getType() != NodeType.SIGNAL && endTime)
            g.drawString(TimeConverter.getLastDigitOfMinutes(interval.getEnd()), (int)endP.getX(), (int)endP.getY());
    }
    
    protected Color getIntervalColor(TimeInterval interval) {
        if (hTrains != null && hTrains.isHighlighedInterval(interval))
            return hTrains.getColor();
        switch (colors) {
            case BY_TYPE:
                return interval.getTrain().getType().getColor();
            case BY_COLOR_CHOOSER:
                return trainColorChooser.getIntervalColor(interval);
            default:
                return Color.black;
        }
    }
    
    protected void addShapeToCollector(TimeInterval interval, Shape shape) {
        if (trainRegionCollector != null) {
            trainRegionCollector.addRegion(interval, shape);
        }
    }
    
    protected void finishCollecting() {
        if (trainRegionCollector != null)
            trainRegionCollector.finishCollecting();
    }
    
    protected boolean isCollectorCollecting(Train train) {
        if (trainRegionCollector == null)
            return false;
        return trainRegionCollector.isCollecting(train);
    }
}
