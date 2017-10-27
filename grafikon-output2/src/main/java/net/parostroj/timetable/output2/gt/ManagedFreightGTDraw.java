package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.output2.gt.DrawUtils.FontInfo;

public class ManagedFreightGTDraw extends GTDrawDecorator {

    private class MFListener implements GTDraw.Listener {
        @Override
        public void trainInStation(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
            Color c = g.getColor();
            Stroke s = g.getStroke();
            // get connections and paint (if exist)
            Collection<FNConnection> fromConnections = freightNet.getTrainsFrom(timeInterval);
            fromConnections = reorderConnections(timeInterval, fromConnections);
            if (!fromConnections.isEmpty()) {
                g.setStroke(connectionStroke);
                for (FNConnection connection : fromConnections) {
                    drawConnection(g, connection, timeInterval, line, true);
                }
            }
            Collection<FNConnection> toConnections = freightNet.getTrainsTo(timeInterval);
            if (!toConnections.isEmpty()) {
                g.setStroke(connectionStroke);
                for (FNConnection connection : toConnections) {
                    drawConnection(g, connection, timeInterval, line, false);
                }
            }
            g.setColor(c);
            g.setStroke(s);
        }

        /**
         * Reverses connections depending on the order in which are needed
         * (first node is in inverse order).
         */
        private Collection<FNConnection> reorderConnections(TimeInterval timeInterval,
                Collection<FNConnection> fromConnections) {
            if (timeInterval.getOwnerAsNode() == firstNode) {
                List<FNConnection> connections = new ArrayList<>(fromConnections);
                Collections.reverse(connections);
                return connections;
            } else {
                return fromConnections;
            }
        }

        @Override
        public void trainOnLine(Graphics2D g, TimeInterval timeInterval, Interval interval, Line2D line) {
            // not interested in lines
        }
    }

    private static final Color CONNECTION_COLOR = Color.RED;
    private static final float CONNECTION_STROKE_WIDTH = 1.5f;

    public static final String HIGHLIGHT_KEY = "mf.highlight";

    public interface Highlight {

        FNConnection getSelectedConnection();

        Color getColor();
    }

    private final GTDraw draw;
    private final RegionCollector<FNConnection> collector;
    private final Highlight highlight;
    private final List<Rectangle> written;

    private final int arrow;
    private final int lineExtend;

    private final Stroke connectionStroke;

    private final Set<Node> routeNodes;
    private final Node firstNode;
    private final FreightNet freightNet;

    public ManagedFreightGTDraw(GTDrawSettings config, GTDraw draw, RegionCollector<FNConnection> collector,
            GTStorage storage) {
        super(draw);
        draw.addListener(new MFListener());
        this.written = new ArrayList<>();
        this.collector = collector;
        this.highlight = storage.getParameter(HIGHLIGHT_KEY, Highlight.class);
        this.draw = draw;
        this.firstNode = draw.getRoute() != null ? draw.getRoute().getFirst() : null;
        this.freightNet = firstNode != null ? firstNode.getDiagram().getFreightNet() : null;
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        arrow = (int) (zoom * 5);
        lineExtend = (int) (zoom * 16);
        connectionStroke = new BasicStroke(zoom * CONNECTION_STROKE_WIDTH);

        routeNodes = new HashSet<>();
        for (Node node : draw.getRoute().getNodes()) {
            routeNodes.add(node);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        this.initFontInfo(g);
        // initialize drawing managed freigth
        this.written.clear();
        if (collector != null) {
            collector.clear();
        }
        // paint diagram
        super.draw(g);
        this.initFontInfo(g);
    }

    private void drawConnection(Graphics2D g, FNConnection conn, TimeInterval interval, Line2D line,
            boolean from) {
        if (highlight != null) {
            if (conn == highlight.getSelectedConnection()) {
                g.setColor(highlight.getColor());
            } else {
                g.setColor(CONNECTION_COLOR);
            }
        }
        int dir = conn.getFrom().getOwnerAsNode() == firstNode ? -1 : 1;
        Point location = getLocation(interval, line, dir);

        String text = this.createText(conn);
        Rectangle bounds = g.getFont().getStringBounds(text, g.getFontRenderContext()).getBounds();
        int width = bounds.width;
        paintText(conn, text, g, new Rectangle(bounds), location, width, dir);
        paintLine(conn, g, location, width, from);
    }

    private boolean collisions(Rectangle rec) {
        boolean collision = false;
        for (Rectangle r : written) {
            if (r.intersects(rec)) {
                collision = true;
                break;
            }
        }
        return collision;
    }

    private void paintText(FNConnection conn, String text, Graphics2D g, Rectangle rectangle, Point point, int width, int dir) {
        rectangle.setLocation(point.x - (width / 2), point.y - fontInfo.height + fontInfo.descent);
        while (collisions(rectangle)) {
            int dy = -rectangle.height * dir;
            rectangle.translate(0, dy);
            point.y += dy;
        }
        g.drawString(text, point.x - width / 2, point.y);
        written.add(rectangle);
        if (collector != null) {
            collector.addRegion(conn, rectangle);
        }
    }

    private void paintLine(FNConnection conn, Graphics2D g, Point point, int width, boolean left) {
        int half = (width + lineExtend) / 2;
        int x1 = point.x - half;
        int y = point.y + fontInfo.descent;
        int x2 = point.x + half;
        Line2D line = new Line2D.Float(x1, y, x2, y);
        g.draw(line);
        // draw arrow
        Polygon p = new Polygon();
        int halfArrow = arrow / 2;
        x2 += halfArrow;
        x1 -= halfArrow;
        if (left) {
            p.addPoint(x2 - arrow, y - arrow);
            p.addPoint(x2, y);
            p.addPoint(x2 - arrow, y + arrow);
        } else {
            p.addPoint(x1, y - arrow);
            p.addPoint(x1 + arrow, y);
            p.addPoint(x1, y + arrow);
        }
        g.fill(p);

        if (collector != null) {
            collector.addRegion(conn, line);
        }
    }

    private String createText(FNConnection conn) {
        return String.format("%s > %s", conn.getFrom().getTrain().getDefaultName(), conn.getTo().getTrain().getDefaultName());
    }

    private Point getLocation(TimeInterval interval, Line2D line, int dir) {
        Node node = interval.getOwnerAsNode();
        List<NodeTrack> tracks = node.getTracks();
        NodeTrack track = dir > 0 ? node.getTracks().get(0) : tracks.get(tracks.size() - 1);
        int y = draw.getY(node, track);
        int x = (int) ((line.getX1() + line.getX2()) / 2);

        // init position depending on direction
        if (dir == -1) {
            y = y + fontInfo.height - fontInfo.descent;
        } else {
            y = y - arrow - fontInfo.descent;
        }

        return new Point(x, y);
    }

    private FontInfo fontInfo;

    protected void initFontInfo(Graphics2D g) {
        if (fontInfo == null) {
            fontInfo = DrawUtils.createFontInfo(g.getFont(), g);
        }
    }

    @Override
    public Refresh processEvent(Event event) {
        Refresh refresh = super.processEvent(event);
        GTDrawEventVisitor visitor = new GTDrawEventVisitor() {
            @Override
            public void visitFreightNetEvent(Event event) {
                setRefresh(Refresh.REPAINT);
            }

            @Override
            public void visitTrainEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE
                        && event.getAttributeChange().checkName(Train.ATTR_MANAGED_FREIGHT)) {
                    setRefresh(Refresh.REPAINT);
                }
            }
        };
        EventProcessing.visit(event, visitor);
        return refresh.update(visitor.getRefresh());
    }
}
