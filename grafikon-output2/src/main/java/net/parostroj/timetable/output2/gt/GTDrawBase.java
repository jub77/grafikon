package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.gt.DrawUtils.FontInfo;
import net.parostroj.timetable.utils.TransformUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * Abstract class for all graphical timetable draws.
 *
 * @author jub
 */
abstract public class GTDrawBase implements GTDraw {

    private static final Logger log = LoggerFactory.getLogger(GTDrawBase.class);

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
    private static final float HHSE_DASH_2 = 7f;

    // other
    private static final int MINIMAL_SPACE_WIDTH = 25;
    private static final float FONT_SIZE = 11f;
    private static final float TITLE_FONT_SIZE_RATIO = 2f;

    // strokes
    protected final Stroke hoursStroke;
    protected final Stroke halfHoursStroke;
    protected final Stroke tenMinutesStroke;
    protected final Stroke halfHoursExtStroke;
    protected final Stroke underlineStroke;

    protected final float minimalSpace;
    protected final float fontSize;

    private final TrainColors colors;
    private final TrainColorChooser trainColorChooser;
    private final TrainRegionCollector trainRegionCollector;

    protected Point start;
    protected Dimension size;
    protected int gapStationX;
    protected int borderX;
    protected int borderY;
    protected int stationNamesPosition = 0;
    protected Map<Node,Integer> positions;
    protected List<Node> stations;
    protected double timeStep;

    protected final Color background;
    protected final int startTime;
    protected final int endTime;

    protected final GTDrawSettings preferences;
    protected final Route route;
    protected final HighlightedTrains hTrains;
    protected final Predicate<TimeInterval> intervalFilter;

    // caching
    private final Map<String, Rectangle> stringBounds = new HashMap<String, Rectangle>();
    private final Map<Node, String> nodeStrings = new HashMap<Node, String>();

    public GTDrawBase(GTDrawSettings config, Route route, TrainRegionCollector collector, Predicate<TimeInterval> intervalFilter) {
        this.route = route;
        this.intervalFilter = intervalFilter;
        this.colors = config.get(GTDrawSettings.Key.TRAIN_COLORS, TrainColors.class);
        this.trainColorChooser = config.get(GTDrawSettings.Key.TRAIN_COLOR_CHOOSER, TrainColorChooser.class);
        this.hTrains = config.get(GTDrawSettings.Key.HIGHLIGHTED_TRAINS, HighlightedTrains.class);
        this.trainRegionCollector = collector;

        // start and end time
        startTime = config.get(GTDrawSettings.Key.START_TIME, Integer.class);
        endTime = config.get(GTDrawSettings.Key.END_TIME, Integer.class);

        // create preferences
        preferences = config;

        // strokes
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        hoursStroke = new BasicStroke(zoom * HOURS_STROKE_WIDTH);
        halfHoursStroke = new BasicStroke(zoom * HALF_HOURS_STROKE_WIDTH);
        tenMinutesStroke = new BasicStroke(zoom * TEN_MINUTES_STROKE_WIDTH);
        underlineStroke = new BasicStroke(zoom * UNDERLINE_STROKE_WIDTH);
        halfHoursExtStroke = new BasicStroke(zoom * HALF_HOURS_STROKE_EXT_WIDTH, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, new float[] { zoom * HHSE_DASH_1, zoom * HHSE_DASH_2 }, 0f);
        minimalSpace = zoom * MINIMAL_SPACE_WIDTH;
        fontSize = zoom * FONT_SIZE;

        background = config.get(GTDrawSettings.Key.BACKGROUND_COLOR, Color.class);
    }

    @Override
    public void draw(Graphics2D g) {
        this.init(g);

        if (preferences.isOption(GTDrawSettings.Key.TITLE)) {
            this.paintTitle(g);
        }
        this.paintHours(g);
        this.paintStations(g);
        g.setColor(Color.BLACK);
        this.paintTrains(g);
        this.paintHoursTexts(g);
        if (!preferences.isOption(GTDrawSettings.Key.DISABLE_STATION_NAMES)) {
            this.paintStationNames(g, stations, positions);
        }
        this.finishCollecting();
    }

    protected void init(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // set font size
        g.setFont(g.getFont().deriveFont(fontSize));

        if (this.start == null) {
            this.updateStartAndSize(g);
        }

        if (positions == null) {
            this.computePositions();
        }
    }

    private void updateStartAndSize(Graphics2D g) {
        // read config
        Integer gapx = preferences.get(GTDrawSettings.Key.STATION_GAP_X, Integer.class);
        Float bx = preferences.get(GTDrawSettings.Key.BORDER_X, Float.class);
        Float by = preferences.get(GTDrawSettings.Key.BORDER_Y, Float.class);
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
                int nameWidth = DrawUtils.getStringWidth(g, name);
                int w = (int) (nameWidth + mSize.getWidth());
                if (w > max) {
                    max = w;
                }
            }
        }
        if (max < this.gapStationX && !preferences.isOption(GTDrawSettings.Key.STATION_GAP_X_FIXED)) {
            this.gapStationX = max;
        }
        // update start
        this.start = new Point(this.borderX, this.borderY);
        this.start.translate(gapStationX, 0);
        if (preferences.isOption(GTDrawSettings.Key.TITLE)) {
            this.start.translate(0, (int) (mSize.getHeight() * TITLE_FONT_SIZE_RATIO));
        }
        // compute size
        Dimension configSize = preferences.get(GTDrawSettings.Key.SIZE, Dimension.class);
        this.size = new Dimension(configSize.width - (this.borderX + this.start.x), configSize.height - (this.borderY + this.start.y));
        // time step
        timeStep = (double) size.width / (endTime - startTime);
    }

    private int computeInitialGapX(Graphics2D g, int gapx) {
        return this.getMSize(g).width * gapx;
    }

    @Override
    public void paintStationNames(Graphics2D g) {
        if (positions == null) {
            this.computePositions();
        }
        this.paintStationNames(g, stations, positions);
    }

    protected abstract void computePositions();

    protected abstract void paintStations(Graphics2D g);

    protected void paintTrains(Graphics2D g) {
        Stroke trainStroke = this.getTrainStroke();
        for (RouteSegment part : route.getSegments()) {
            if (part.asNode() != null) {
                this.paintTrainsInStation(part.asNode(), g, trainStroke);
            } else if (part.asLine() != null) {
                this.paintTrainsOnLine(part.asLine(), g, trainStroke);
            }
        }
    }

    protected abstract void paintTrainsInStation(Node asNode, Graphics2D g, Stroke trainStroke);

    @Override
    public Route getRoute() {
        return route;
    }

    @Override
    public void setStationNamesPosition(int position) {
        this.stationNamesPosition = position;
    }

    protected void paintTitle(Graphics2D g) {
        Font currentFont = g.getFont();
        g.setFont(currentFont.deriveFont(Font.BOLD, fontSize * TITLE_FONT_SIZE_RATIO));
        int y = (int) (start.y - this.getMSize(g).getHeight());
        String text = preferences.get(GTDrawSettings.Key.TITLE_TEXT, String.class);
        if (text == null) {
            text = this.route.getName();
            if (text == null) {
                text = this.route.toString();
            }
        }
        Dimension configSize = preferences.get(GTDrawSettings.Key.SIZE, Dimension.class);
        int width = configSize.width - 2 * borderX;
        text = DrawUtils.getStringForWidth(g, text, width);
        int textWidth = DrawUtils.getStringWidth(g, text);
        int shiftX = (width - textWidth) / 2;
        int shiftY = DrawUtils.createFontInfo(g).descent;
        g.drawString(text, borderX + shiftX, y - shiftY);
        // restore font
        g.setFont(currentFont);
    }

    protected void paintHours(Graphics2D g) {
        int yEnd = start.y + size.height;
        // half hour step
        double step = 1800 * timeStep;
        int time = 0;

        // draw hours
        for (int i = 0; i < 48; i++) {
            int timeToPaint = time < startTime ? time + TimeInterval.DAY : time;
            this.paintHourAndHalfHourOnTime(g, yEnd, step, i, timeToPaint);
            if (timeToPaint == startTime) {
                this.paintHourAndHalfHourOnTime(g, yEnd, step, i, timeToPaint + TimeInterval.DAY);
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
            for (int i = 0; i <= 24 * 6; i++) {
                int timeToPaint = time < startTime ? time + TimeInterval.DAY : time;
                this.paintTenMinutesOnTime(g, yEnd, i, timeToPaint);
                if (timeToPaint == startTime) {
                    this.paintTenMinutesOnTime(g, yEnd, i, timeToPaint + TimeInterval.DAY);
                }
                time += 600;
            }
        }
    }

    private void paintHourAndHalfHourOnTime(Graphics2D g, int yEnd, double step, int i, int timeToPaint) {
        if (isTimeVisible(timeToPaint)) {
            int xLocation = this.getX(timeToPaint);
            if ((i & 1) == 0) {
                // hours
                g.setColor(Color.orange);
                g.setStroke(hoursStroke);
            } else {
                // half hours
                g.setColor(Color.orange);
                if (preferences.isOption(GTDrawSettings.Key.EXTENDED_LINES)) {
                    g.setStroke(halfHoursExtStroke);
                } else {
                    g.setStroke(halfHoursStroke);
                }
            }

            if (((i & 1) != 1) || step >= minimalSpace) {
                // draw line
                g.drawLine(xLocation, start.y, xLocation, yEnd);
            }
        }
    }

    private void paintTenMinutesOnTime(Graphics2D g, int yEnd, int i, int timeToPaint) {
        if ((i % 3 != 0) && this.isTimeVisible(timeToPaint)) {
            int xLocation = this.getX(timeToPaint);
            // draw line
            g.drawLine(xLocation, start.y, xLocation, yEnd);
        }
    }

    protected void paintHoursTexts(Graphics2D g) {
        int time = 0;
        for (int h = 0; h < 24; h++) {
            int timeToPaint = time < startTime ? time + TimeInterval.DAY : time;
            this.paintHourTextOnTime(g, h, timeToPaint);
            if (timeToPaint == startTime) {
                this.paintHourTextOnTime(g, h, timeToPaint + TimeInterval.DAY);
            }
            time += 3600;
        }
    }

    private void paintHourTextOnTime(Graphics2D g, int h, int timeToPaint) {
        if (isTimeVisible(timeToPaint)) {
            int xLocation = this.getX(timeToPaint);
            // draw hours
            g.setColor(Color.black);

            String hStr = Integer.toString(h);
            Rectangle rr = this.getStringBounds(hStr, g);
            g.drawString(hStr, xLocation - rr.width / 2 + 1, start.y - 3);
        }
    }

    protected Line2D createTrainLine(TimeInterval interval, Interval i) {
        int x1 = this.getX(i.getStart());
        int x2 = this.getX(i.getEnd());
        int y1 = this.getY(interval.getPreviousTrainInterval());
        int y2 = this.getY(interval.getNextTrainInterval());

        Line2D line2D = new Line2D.Double(x1, y1, x2, y2);
        return line2D;
    }

    protected Line2D createTrainLineInStation(TimeInterval interval, Interval i) {
        int y = this.getY(interval);
        int x1 = this.getX(i.getStart());
        int x2 = this.getX(i.getEnd());
        Line2D line2D = new Line2D.Float(x1, y, x2, y);
        return line2D;
    }

    protected void paintTrainsOnLine(Line line, Graphics2D g, Stroke trainStroke) {
        g.setStroke(trainStroke);
        for (LineTrack track : line.getTracks()) {
            for (TimeInterval interval : track.getTimeIntervalList()) {
                if (intervalFilter == null || intervalFilter.apply(interval)) {
                    boolean paintTrainName = (interval.getFrom().getType() != NodeType.SIGNAL)
                            && (preferences.isOption(GTDrawSettings.Key.TRAIN_NAMES));
                    boolean paintMinutes = preferences.isOption(GTDrawSettings.Key.ARRIVAL_DEPARTURE_DIGITS);
                    Interval normalized = interval.getInterval().normalize();
                    if (this.isTimeVisible(normalized.getStart(), normalized.getEnd())) {
                        g.setColor(this.getIntervalColor(interval));
                        this.paintTrainOnLineWithInterval(g, paintTrainName, paintMinutes, interval, normalized);
                    }
                    Interval overMidnight = normalized.getComplementatyIntervalOverThreshold(startTime);
                    if (overMidnight != null && this.isTimeVisible(overMidnight.getStart(), overMidnight.getEnd())) {
                        g.setColor(this.getIntervalColor(interval));
                        this.paintTrainOnLineWithInterval(g, paintTrainName, paintMinutes, interval, overMidnight);
                    }
                }
            }
        }
        g.setColor(Color.BLACK);
    }

    protected void paintTrainOnLineWithInterval(Graphics2D g, boolean paintTrainName, boolean paintMinutes, TimeInterval interval, Interval i) {
        Line2D line2D = this.createTrainLine(interval, i);

        // add shape to collector
        if (this.isCollectorCollecting(interval.getTrain())) {
            this.addShapeToCollector(interval, line2D);
        }

        g.draw(line2D);

        if (paintTrainName) {
            this.paintTrainNameOnLine(g, interval, line2D);
        }
        if (paintMinutes) {
            this.paintMinutesOnLine(g, interval, line2D);
        }

    }

    protected void paintTrainInStationWithInterval(Graphics2D g, TimeInterval interval) {
        boolean isCollected = this.isCollectorCollecting(interval.getTrain());

        Interval normalized = interval.getInterval().normalize();
        if (this.isTimeVisible(normalized.getStart(), normalized.getEnd())) {
            Line2D line = this.createTrainLineInStation(interval, normalized);
            if (isCollected) {
                this.addShapeToCollector(interval, line);
            }
            g.draw(line);
        }
        Interval overMidnight = normalized.getComplementatyIntervalOverThreshold(startTime);
        if (overMidnight != null && this.isTimeVisible(overMidnight.getStart(), overMidnight.getEnd())) {
            Line2D line = this.createTrainLineInStation(interval, overMidnight);
            if (isCollected) {
                this.addShapeToCollector(interval, line);
            }
            g.draw(line);
        }
    }

    protected void paintStationNames(Graphics2D g, List<Node> stations, Map<Node, Integer> positions) {
        g.setFont(g.getFont().deriveFont(fontSize));
        for (Node s : stations) {
            // ignore signals
            if (s.getType() == NodeType.SIGNAL) {
                continue;
            }
            String name = null;
            int y = this.getY(s, null);
            // draw name of the station
            if (!nodeStrings.containsKey(s)) {
                String origName = TransformUtil.transformStation(s, null, null).trim();
                name = DrawUtils.getStringForWidth(g, origName, gapStationX);
                nodeStrings.put(s, name);
            } else {
                name = nodeStrings.get(s);
            }
            Rectangle b = this.getStringBounds(name, g);
            FontInfo fi = this.getFontInfo(g);
            int sx = this.borderX + stationNamesPosition;
            int sy = y - fi.strikeThrough;
            int ow = Math.round(this.getMSize(g).width / 5);
            Rectangle r2 = new Rectangle(sx - ow, sy + fi.descent - fi.height - ow, b.width + 2 * ow, fi.height + 2 * ow);
            if (background != null) {
                g.setColor(background);
                g.fill(r2);
            }
            g.setColor(Color.black);
            g.drawString(name, sx, sy);
        }
    }

    protected void paintTrainNameOnLine(Graphics2D g, TimeInterval interval, Line2D line) {
        // draw train name
        AffineTransform old = g.getTransform();
        double lengthY = line.getY2() - line.getY1();
        double lengthX = line.getX2() - line.getX1();
        // get length
        int length = (int) Math.round(Math.sqrt(Math.pow(lengthY, 2) + Math.pow(lengthX, 2)));
        AffineTransform newTransform = g.getTransform();
        newTransform.translate(line.getX1(), line.getY1());
        newTransform.rotate(lengthX, lengthY);
        g.setTransform(newTransform);
        // length of the text
        Train train = interval.getTrain();
        String trainName = train.getName();
        Rectangle rr = this.getStringBounds(trainName, g);
        Shape nameShape = null;

        int shift = (length - rr.width) / 2;
        int above = this.getMSize(g).height / 3;
        FontInfo fi = this.getFontInfo(g);
        // ensure half M size before and after train name
        if (length - (rr.width + this.getMSize(g).width) > 0) {
            g.drawString(trainName, shift, -above);
            if (this.isCollectorCollecting(train)) {
                rr.translate(shift, -(above - fi.descent + fi.height));
                nameShape = newTransform.createTransformedShape(rr);
                try {
                    nameShape = old.createInverse().createTransformedShape(nameShape);
                } catch (Exception e) {
                    log.error("Error transform name shape.", e);
                    nameShape = null;
                }
            }
        }
        g.setTransform(old);

        if (nameShape != null) {
            this.addShapeToCollector(interval, nameShape);
        }
    }

    private Rectangle digitSize;
    private Rectangle mSize;
    private FontInfo fontInfo;

    protected Rectangle getDigitSize(Graphics2D g) {
        if (digitSize == null) {
            digitSize = this.getCharSize(DIGIT_CHAR, g);
        }
        return digitSize;
    }

    protected Rectangle getMSize(Graphics2D g) {
        if (mSize == null) {
            mSize = this.getCharSize(M_CHAR, g);
        }
        return mSize;
    }

    protected FontInfo getFontInfo(Graphics2D g) {
        if (fontInfo == null) {
            fontInfo = DrawUtils.createFontInfo(g.getFont(), g);
        }
        return fontInfo;
    }

    private Rectangle getCharSize(String ch, Graphics2D g) {
        FontMetrics fm = g.getFontMetrics();
        FontInfo info = this.getFontInfo(g);
        return new Rectangle(fm.stringWidth(ch), info.height);
    }

    protected Rectangle getStringBounds(String str, Graphics2D g) {
        Rectangle rectangle = this.stringBounds.get(str);
        if (rectangle == null) {
            rectangle = new Rectangle(g.getFontMetrics().stringWidth(str), getFontInfo(g).height);
        }
        return rectangle;
    }

    protected void paintMinutesOnLine(Graphics2D g, TimeInterval interval, Line2D line) {
        // check if I should draw end time
        boolean endTimeCheck = true;
        if (!interval.getTrainInterval(1).isStop()) {
            Train train = interval.getTrain();
            int ind = train.getTimeIntervalList().indexOf(interval);
            if ((ind + 2) < train.getTimeIntervalList().size()) {
                TimeInterval nextLineInterval = train.getTimeIntervalList().get(ind + 2);
                Node endNode2 = nextLineInterval.getTo();
                if (stations.contains(endNode2)) {
                    endTimeCheck = false;
                }
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
            int lastDigit = c.getLastDigitOfMinutes(interval.getStart());
            g.drawString(Integer.toString(lastDigit), xp, yp);
            if (c.isHalfMinute(interval.getStart())) {
            	drawUnderscore(g, xp, yp, dSize.getWidth());
            }
        }
        if (interval.getTo().getType() != NodeType.SIGNAL && endTimeCheck) {
            int xp = (int) endP.getX();
            int yp = (int) endP.getY();
            int lastDigit = c.getLastDigitOfMinutes(interval.getEnd());
            g.drawString(Integer.toString(lastDigit), xp, yp);
            if (c.isHalfMinute(interval.getEnd())) {
            	drawUnderscore(g, xp, yp, dSize.getWidth());
            }
        }
    }

    private void drawUnderscore(Graphics2D g, int xp, int yp, double wp) {
    	yp = (int) (yp + wp / 4);
    	g.setStroke(underlineStroke);
    	g.drawLine(xp, yp, (int) (xp + wp), yp);
    }

    protected Color getIntervalColor(TimeInterval interval) {
        if (hTrains != null && hTrains.isHighlighedInterval(interval)) {
            return hTrains.getColor(interval);
        }
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
        if (trainRegionCollector != null) {
            trainRegionCollector.finishCollecting();
        }
    }

    protected boolean isCollectorCollecting(Train train) {
        if (trainRegionCollector == null) {
            return false;
        }
        return trainRegionCollector.isCollecting(train);
    }

    protected TimeConverter getTimeConverter(TimeInterval interval) {
    	return interval.getTrain().getDiagram().getTimeConverter();
    }

    @Override
    public int getX(int time) {
        int x = start != null ? (int)(start.x + (time - startTime) * timeStep) : -1;
        return x;
    }

    @Override
    public int getY(Node node, Track track) {
        Integer position = positions.get(node);
        return position != null ? start.y + position : -1;
    }

    @Override
    public int getY(TimeInterval interval) {
        return this.getY(interval.getOwnerAsNode(), interval.getTrack());
    }

    @Override
    public Dimension getSize() {
        return preferences.get(GTDrawSettings.Key.SIZE, Dimension.class);
    }

    protected boolean isTimeVisible(int time1, int time2) {
        return isTimeVisible(time1) || isTimeVisible(time2);
    }

    protected boolean isTimeVisible(int time) {
        return startTime <= time && time <= endTime;
    }

    @Override
    public void changed(Change change, Object object) {
        switch(change) {
            case REMOVED_TRAIN: case TRAIN_TEXT_CHANGED:
                stringBounds.remove(((Train) object).getName());
                break;
            case NODE_TEXT_CHANGED:
                stringBounds.remove(((Node) object).getName());
                nodeStrings.remove(object);
                break;
            case ALL_TRAIN_TEXTS_CHANGED:
                stringBounds.clear();
                break;
            case TRAIN_INTERVALS_CHANGED:
                // nothing
                break;
        }
    }

    abstract protected Stroke getTrainStroke();
}
