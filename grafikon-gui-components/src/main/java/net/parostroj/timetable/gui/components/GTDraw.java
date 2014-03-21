package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.model.*;
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

    // chars
    private static final String M_CHAR = "M";
    private static final String DIGIT_CHAR = "0";

    // basic display
    private static final float HOURS_STROKE_WIDTH = 1.8f;
    private static final float HALF_HOURS_STROKE_WIDTH = 0.9f;
    private static final float TEN_MINUTES_STROKE_WIDTH = 0.4f;
    private static final float UNDERLINE_STROKE_WIDTH = 1.4f;

    // extended display
    private static final float HALF_HOURS_STROKE_EXT_WIDTH = 1.1f;
    private static final float HHSE_DASH_1 = 15f;
    private static final float HHSE_DAST_2 = 7f;

    // other
    private static final int MINIMAL_SPACE_WIDTH = 25;
    private static final float FONT_SIZE = 11f;

    // strokes
    protected final Stroke hoursStroke;
    protected final Stroke halfHoursStroke;
    protected final Stroke tenMinutesStroke;
    protected final Stroke halfHoursExtStroke;
    protected final Stroke underlineStroke;

    protected final float minimalSpace;
    protected final float fontSize;

    protected Point start;
    protected Dimension size;
    protected int gapStationX;
    protected int borderX;
    protected int borderY;
    protected Route route;
    protected int positionX = 0;
    protected HighlightedTrains hTrains;
    private final GTViewSettings.TrainColors colors;
    private final TrainColorChooser trainColorChooser;
    private final TrainRegionCollector trainRegionCollector;
    protected GTViewSettings preferences;
    protected Map<Node,Integer> positions;
    protected List<Node> stations;
    protected Color background = Color.white;
    protected int startTime;
    protected int endTime;
    protected double timeStep;

    // caching
    private final Map<Node, TextLayout> nodeTexts = new HashMap<Node, TextLayout>();
    private final Map<Train, TextLayout> trainTexts = new HashMap<Train, TextLayout>();

    public GTDraw(GTViewSettings config, Route route, TrainRegionCollector collector) {
        this.route = route;
        this.colors = config.get(GTViewSettings.Key.TRAIN_COLORS, GTViewSettings.TrainColors.class);
        this.trainColorChooser = config.get(GTViewSettings.Key.TRAIN_COLOR_CHOOSER, TrainColorChooser.class);
        this.hTrains = config.get(GTViewSettings.Key.HIGHLIGHTED_TRAINS, HighlightedTrains.class);
        this.trainRegionCollector = collector;

        // start and end time
        Integer st = config.get(GTViewSettings.Key.START_TIME, Integer.class);
        Integer et = config.get(GTViewSettings.Key.END_TIME, Integer.class);
        boolean ignore = config.getOption(GTViewSettings.Key.IGNORE_TIME_LIMITS);
        startTime = (st != null && !ignore) ? st : 0;
        endTime = (et != null && !ignore) ? et : TimeInterval.DAY;

        // create preferences
        preferences = config;

        // strokes
        Float zoom = config.get(Key.ZOOM, Float.class);
        hoursStroke = new BasicStroke(zoom * HOURS_STROKE_WIDTH);
        halfHoursStroke = new BasicStroke(zoom * HALF_HOURS_STROKE_WIDTH);
        tenMinutesStroke = new BasicStroke(zoom * TEN_MINUTES_STROKE_WIDTH);
        underlineStroke = new BasicStroke(zoom * UNDERLINE_STROKE_WIDTH);
        halfHoursExtStroke = new BasicStroke(zoom * HALF_HOURS_STROKE_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { zoom * HHSE_DASH_1, zoom * HHSE_DAST_2 }, 0f);
        minimalSpace = zoom * MINIMAL_SPACE_WIDTH;
        fontSize = zoom * FONT_SIZE;
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

        // set font size
        g.setFont(g.getFont().deriveFont(fontSize));

        if (this.start == null)
            this.updateStartAndSize(g);

        if (positions == null)
            this.computePositions();

        this.paintHours(g);
        this.paintStations(g);
        g.setColor(Color.BLACK);
        this.paintTrains(g);
        if (preferences.getOption(Key.DISABLE_STATION_NAMES) != Boolean.TRUE) {
            this.paintStationNames(g, stations, positions);
        }
        this.finishCollecting();
    }

    private void updateStartAndSize(Graphics2D g) {
        // read config
        Integer gapx = preferences.get(GTViewSettings.Key.STATION_GAP_X, Integer.class);
        Float bx = preferences.get(GTViewSettings.Key.BORDER_X, Float.class);
        Float by = preferences.get(GTViewSettings.Key.BORDER_Y, Float.class);
        Rectangle2D mSize = getMSize(g);
        this.borderX = (int) (mSize.getWidth() * bx);
        this.borderY = (int) (mSize.getHeight() * by);
        this.gapStationX = this.computeInitialGapX(g, gapx); // initial size ...
        // correct gap by station names
        int max = 0;
        for (RouteSegment seg : getRoute().getSegments()) {
            if (seg.isNode()) {
                Node n = seg.asNode();
                String name = TransformUtil.transformStation(n, null, null).trim();
                Rectangle2D b = g.getFont().getStringBounds(name, g.getFontRenderContext());
                int w = (int) (b.getWidth() + mSize.getWidth());
                if (w > max) {
                    max = w;
                }
            }
        }
        if (max < this.gapStationX) {
            this.gapStationX = max;
        }
        // update start
        this.start = new Point(this.borderX, this.borderY);
        this.start.translate(gapStationX, 0);
        // compute size
        Dimension configSize = preferences.get(GTViewSettings.Key.SIZE, Dimension.class);
        this.size = new Dimension(configSize.width - (this.borderX * 2 + this.gapStationX), configSize.height - this.borderY * 2);
        // time step
        timeStep = (double) size.width / (endTime - startTime);

        // prepare cached minutes
        this.prepareCached(g);
    }

    private void prepareCached(Graphics2D g) {
    }

    private int computeInitialGapX(Graphics2D g, int gapx) {
        String w = CharBuffer.allocate(10).toString().replace('\0', 'M');
        return (int) g.getFont().getStringBounds(w, g.getFontRenderContext()).getWidth();
    }

    public void paintStationNames(Graphics g) {
        if (positions == null)
            this.computePositions();
        this.paintStationNames((Graphics2D) g, stations, positions);
    }

    protected abstract void computePositions();

    protected abstract void paintStations(Graphics2D g);

    protected abstract void paintTrains(Graphics2D g);

    public Route getRoute() {
        return route;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    protected void paintHours(Graphics2D g) {
        int yEnd = start.y + size.height;
        // half hour step
        double step = 1800 * timeStep;
        int time = 0;

        // draw hours
        for (int i = 0; i <= 48; i++) {
            if (isTimeVisible(time)) {
                int xLocation = this.getX(time);
                if ((i & 1) == 0) {
                    // hours
                    g.setColor(Color.orange);
                    g.setStroke(hoursStroke);
                } else {
                    // half hours
                    g.setColor(Color.orange);
                    if (preferences.get(GTViewSettings.Key.EXTENDED_LINES) == Boolean.TRUE)
                        g.setStroke(halfHoursExtStroke);
                    else
                        g.setStroke(halfHoursStroke);
                }

                if (((i & 1) != 1) || step >= minimalSpace)
                    // draw line
                    g.drawLine(xLocation, start.y, xLocation, yEnd);

                if ((i & 1) == 0) {
                    // draw hours
                    g.setColor(Color.black);
                    String text = Integer.toString(i / 2);
                    Rectangle2D rr = g.getFont().getStringBounds(text, g.getFontRenderContext());
                    g.drawString(text, xLocation - (int) (rr.getWidth() / 2) + 1, start.y - 3);
                }
            }

            // half an hour
            time += 1800;
        }

        // 10 minutes
        double tenMinutesStep = 600 * timeStep;
        time = 0;
        if (tenMinutesStep >= minimalSpace) {
            g.setColor(Color.orange);
            g.setStroke(tenMinutesStroke);
            for (int i = 0; i <= 24*6; i++) {
                if ((i % 3 !=0) && this.isTimeVisible(time)) {
                    int xLocation = this.getX(time);
                    // draw line
                    g.drawLine(xLocation, start.y, xLocation, yEnd);
                }
                time += 600;
            }
        }
    }

    abstract protected Line2D createTrainLine(TimeInterval interval, Interval i);

    protected void paintTrainsOnLine(Line line, Graphics2D g, Stroke trainStroke) {
        g.setStroke(trainStroke);
        for (LineTrack track : line.getTracks()) {
            for (TimeInterval interval : track.getTimeIntervalList()) {
                boolean paintTrainName = (interval.getFrom().getType() != NodeType.SIGNAL) &&
                        (preferences.get(GTViewSettings.Key.TRAIN_NAMES) == Boolean.TRUE);
                boolean paintMinutes = preferences.get(GTViewSettings.Key.ARRIVAL_DEPARTURE_DIGITS) == Boolean.TRUE;

                Interval normalized = interval.getInterval().normalize();
                if (this.isTimeVisible(normalized.getStart(), normalized.getEnd())) {
                    g.setColor(this.getIntervalColor(interval));
                    this.paintTrainOnLineWithInterval(g, paintTrainName, paintMinutes, interval, normalized);
                }
                Interval overMidnight = normalized.getNonNormalizedIntervalOverMidnight();
                if (overMidnight != null && this.isTimeVisible(overMidnight.getStart(), overMidnight.getEnd())) {
                    g.setColor(this.getIntervalColor(interval));
                    this.paintTrainOnLineWithInterval(g, paintTrainName, paintMinutes, interval, overMidnight);
                }
            }
        }
        g.setColor(Color.BLACK);
    }

    private void paintTrainOnLineWithInterval(Graphics2D g, boolean paintTrainName, boolean paintMinutes, TimeInterval interval, Interval i) {
        Line2D line2D = this.createTrainLine(interval, i);

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
            String name = TransformUtil.transformStation(s, null, null).trim();
            int y = start.y + positions.get(s);
            // draw name of the station
            Font f = g.getFont();
            Rectangle2D b = null;
            if (!nodeTexts.containsKey(s)) {
                String transName = name;
                int nameLength = name.length();
                while (b == null || b.getWidth() >= gapStationX) {
                    b = f.getStringBounds(transName, g.getFontRenderContext());
                    if (b.getWidth() >= gapStationX) {
                        nameLength -= 1;
                        transName = name.substring(0, nameLength);
                        transName += "...";
                    }
                }
                nodeTexts.put(s, new TextLayout(transName, g.getFont(), g.getFontRenderContext()));
            }
            TextLayout tl = nodeTexts.get(s);
            Rectangle2D r = tl.getBounds();
            Rectangle2D r2 = new Rectangle2D.Float(this.borderX + positionX, (float) (y + r.getY() - 1 + r.getHeight() / 2),
                    (float) (r.getWidth() + 2), (float) (r.getHeight() + 2));
            g.setColor(background);
            g.fill(r2);
            g.setColor(Color.black);
            tl.draw(g, (float) (r2.getX() + 1), (float) (r2.getY() + 1 - r.getY()));
        }
    }

    protected void paintTrainNameOnLine(Graphics2D g, TimeInterval interval, Line2D line) {
        // draw train name
        AffineTransform old = g.getTransform();
        double lengthY = line.getY2() - line.getY1();
        double lengthX = line.getX2() - line.getX1();
        // get length
        double length = Math.sqrt(Math.pow(lengthY, 2) + Math.pow(lengthX, 2));
        AffineTransform newTransform = g.getTransform();
        newTransform.translate(line.getX1(), line.getY1());
        newTransform.rotate(lengthX, lengthY);
        g.setTransform(newTransform);
        // length of the text
        Train train = interval.getTrain();
        if (!trainTexts.containsKey(train)) {
            trainTexts.put(train, new TextLayout(train.getName(), g.getFont(), g.getFontRenderContext()));
        }
        TextLayout layout = trainTexts.get(train);
        Rectangle2D rr = layout.getBounds();
        Shape nameShape = null;

        int shift = (int)(length - rr.getWidth()) / 2;
        if (shift >= 0) {
            layout.draw(g, shift, -5);
            if (this.isCollectorCollecting(train)) {
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
    private Rectangle2D mSize;

    private Rectangle2D getDigitSize(Graphics2D g) {
        if (digitSize == null) {
            digitSize = g.getFont().getStringBounds(DIGIT_CHAR, g.getFontRenderContext());
        }
        return digitSize;
    }

    private Rectangle2D getMSize(Graphics2D g) {
        if (mSize == null) {
            mSize = g.getFont().getStringBounds(M_CHAR, g.getFontRenderContext());
        }
        return mSize;
    }

    protected void paintMinutesOnLine(Graphics2D g, TimeInterval interval, Line2D line) {
        // check if I should draw end time
        boolean endTimeCheck = true;
        if (!interval.getNextTrainInterval().isStop()) {
            Train train = interval.getTrain();
            int ind = train.getTimeIntervalList().indexOf(interval);
            if ((ind + 2) < train.getTimeIntervalList().size()) {
                TimeInterval nextLineInterval = train.getTimeIntervalList().get(ind + 2);
                Node endNode2 = nextLineInterval.getTo();
                if (stations.contains(endNode2))
                    endTimeCheck = false;
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
        TimeConverter c = this.getTimeConverter(interval);
        if (interval.getFrom().getType() != NodeType.SIGNAL) {
            int xp = (int) startP.getX();
            int yp = (int) startP.getY();
            g.drawString(c.getLastDigitOfMinutes(interval.getStart()), xp, yp);
            if (c.isHalfMinute(interval.getStart()))
            	drawUnderscore(g, xp, yp, dSize.getWidth());
        }
        if (interval.getTo().getType() != NodeType.SIGNAL && endTimeCheck) {
            int xp = (int) endP.getX();
            int yp = (int) endP.getY();
            g.drawString(c.getLastDigitOfMinutes(interval.getEnd()), xp, yp);
            if (c.isHalfMinute(interval.getEnd()))
            	drawUnderscore(g, xp, yp, dSize.getWidth());
        }
    }

    private void drawUnderscore(Graphics2D g, int xp, int yp, double wp) {
    	yp = (int) (yp + wp / 4);
    	g.setStroke(underlineStroke);
    	g.drawLine(xp, yp, (int) (xp + wp), yp);
    }

    protected Color getIntervalColor(TimeInterval interval) {
        if (hTrains != null && hTrains.isHighlighedInterval(interval))
            return hTrains.getColor();
        switch (colors) {
            case BY_TYPE:
                return interval.getTrain().getType() != null ? interval.getTrain().getType().getColor() : Color.black;
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

    protected TimeConverter getTimeConverter(TimeInterval interval) {
    	return interval.getTrain().getTrainDiagram().getTimeConverter();
    }

    protected int getX(int time) {
        int x = (int)(start.x + (time - startTime) * timeStep);
        return x;
    }

    protected boolean isTimeVisible(int time1, int time2) {
        return isTimeVisible(time1) || isTimeVisible(time2);
    }

    protected boolean isTimeVisible(int time) {
        return startTime <= time && time <= endTime;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void removedTrain(Train train) {
        trainTexts.remove(train);
    }

    public void changedTextTrain(Train train) {
        trainTexts.remove(train);
    }

    public void changedTextNode(Node node) {
        nodeTexts.remove(node);
    }

    public void changedTextAllTrains() {
        trainTexts.clear();
    }
}
