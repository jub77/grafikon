package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.gt.DrawUtils.FontInfo;
import net.parostroj.timetable.utils.Tuple;

public class ManagedFreightGTDraw extends GTDrawDecorator {

    private static final Color CONNECTION_COLOR = Color.RED;
    private static final float CONNECTION_STROKE_WIDTH = 1.5f;

    public interface Highlight {
        FNConnection getSelected();

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

    public ManagedFreightGTDraw(GTDrawSettings config, GTDraw draw, RegionCollector<FNConnection> collector, Highlight highlight) {
        super(draw);
        this.written = new ArrayList<Rectangle>();
        this.collector = collector;
        this.highlight = highlight;
        this.draw = draw;
        Float zoom = config.get(GTDrawSettings.Key.ZOOM, Float.class);
        arrow = (int) (zoom * 5);
        lineExtend = (int) (zoom * 16);
        connectionStroke = new BasicStroke(zoom * CONNECTION_STROKE_WIDTH);

        routeNodes = new HashSet<Node>();
        for (Node node : draw.getRoute().getNodes()) {
            routeNodes.add(node);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // paint diagram
        super.draw(g);
        this.initFontInfo(g);
        // draw managed trains ...
        this.written.clear();
        g.setStroke(connectionStroke);

        Route route = draw.getRoute();
        Node firstNode = route.getSegments().get(0).asNode();
        FreightNet net = firstNode.asNode().getDiagram().getFreightNet();

        if (collector != null) {
            collector.clear();
        }
        g.setColor(Color.magenta);
        for (Node node : route.getNodes()) {
            for (FNConnection conn : net.getConnections(node)) {
                drawConnection(g, firstNode, conn);
            }
        }
    }

    private void drawConnection(Graphics2D g, Node firstNode, FNConnection conn) {
        if (highlight != null) {
            if (conn == highlight.getSelected()) {
                g.setColor(highlight.getColor());
            } else {
                g.setColor(CONNECTION_COLOR);
            }
        }
        int dir = conn.getFrom().getOwnerAsNode() == firstNode ? -1 : 1;
        Tuple<Point> location = getLocation(conn, dir);
        String text = this.createText(conn);
        Rectangle bounds = g.getFont().getStringBounds(text, g.getFontRenderContext()).getBounds();
        int width = bounds.width;
        if (location.first != null) {
            paintText(conn, text, g, new Rectangle(bounds), location.first, width, dir);
            paintLine(conn, g, location.first, width, true, dir);
        }
        if (location.second != null) {
            paintText(conn, text, g, new Rectangle(bounds), location.second, width, dir);
            paintLine(conn, g, location.second, width, false, dir);
        }
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
            int dy = - rectangle.height * dir;
            rectangle.translate(0, dy);
            point.y += dy;
        }
        g.drawString(text, point.x - width / 2, point.y);
        written.add(rectangle);
        if (collector != null) {
            collector.addRegion(conn, rectangle);
        }
    }

    private void paintLine(FNConnection conn, Graphics2D g, Point point, int width, boolean left, int dir) {
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
        return String.format("%s > %s", conn.getFrom().getTrain().getName(), conn.getTo().getTrain().getName());
    }

    private Tuple<Point> getLocation(FNConnection conn, int dir) {
        Node node = conn.getFrom().getOwnerAsNode();
        List<NodeTrack> tracks = node.getTracks();
        NodeTrack track = dir > 0 ? node.getTracks().get(0) : tracks.get(tracks.size() - 1);
        int y = draw.getY(node, track);
        int x1 = draw.getX((conn.getFrom().getStart() + conn.getFrom().getEnd()) / 2);
        int x2 = draw.getX((conn.getTo().getStart() + conn.getTo().getEnd()) / 2);

        // init position depending on direction
        if (dir == -1) {
            y = y + fontInfo.height - fontInfo.descent;
        } else {
            y = y - arrow - fontInfo.descent;
        }

        Point firstPoint = this.isWithLine(conn.getFrom()) ? new Point(x1, y) : null;
        Point secondPoint = this.isWithLine(conn.getTo()) ? new Point(x2, y) : null;
        return new Tuple<Point>(firstPoint, secondPoint);
    }

    private boolean isWithLine(TimeInterval interval) {
        TimeInterval prevNodeInterval = interval.getTrainInterval(-2);
        TimeInterval nextNodeInterval = interval.getTrainInterval(2);
        if (prevNodeInterval != null) {
            if (routeNodes.contains(prevNodeInterval.getOwnerAsNode())) {
                return true;
            }
        }
        if (nextNodeInterval != null) {
            if (routeNodes.contains(nextNodeInterval.getOwnerAsNode())) {
                return true;
            }
        }
        return false;
    }

    private FontInfo fontInfo;

    protected void initFontInfo(Graphics2D g) {
        if (fontInfo == null) {
            fontInfo = DrawUtils.createFontInfo(g.getFont(), g);
        }
    }
}
