package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Tuple;

public class ManagedFreightGTDraw extends GTDrawDecorator {

    public interface Highlight {
        FNConnection getSelected();

        Color getColor();
    }

    private GTDrawBase drawBase;
    private final RegionCollector<FNConnection> collector;
    private final Highlight highlight;
    private final List<Rectangle> written;

    private final int arrow;
    private final int lineExtend;

    public ManagedFreightGTDraw(GTViewSettings config, GTDraw draw, RegionCollector<FNConnection> collector, Highlight highlight) {
        super(draw);
        this.written = new ArrayList<Rectangle>();
        this.collector = collector;
        this.highlight = highlight;
        if (draw instanceof GTDrawBase) {
            drawBase = (GTDrawBase) draw;
        } else {
            throw new IllegalArgumentException("Has to be instance of base gt draw.");
        }
        Float zoom = config.get(Key.ZOOM, Float.class);
        arrow = (int) (zoom * 5);
        lineExtend = (int) (zoom * 16);
    }

    @Override
    public void draw(Graphics2D g) {
        // paint diagram
        super.draw(g);
        // draw managed trains ...
        this.written.clear();
        g.setStroke(drawBase.getTrainStroke());

        Route route = drawBase.getRoute();
        RouteSegment fSegment = route.getSegments().get(0);
        FreightNet net = fSegment.asNode().getTrainDiagram().getFreightNet();

        Collection<FNConnection> connections = net.getConnections();
        drawBase.init(g);
        if (collector != null) {
            collector.clear();
        }
        g.setColor(Color.magenta);
        for (FNConnection conn : connections) {
            if (drawBase.positions.containsKey(conn.getFrom().getOwnerAsNode())) {
                if (highlight != null) {
                    if (conn == highlight.getSelected()) {
                        g.setColor(highlight.getColor());
                    } else {
                        g.setColor(Color.magenta);
                    }
                }
                int dir = conn.getFrom().getOwnerAsNode() == fSegment ? -1 : 1;
                Tuple<Point> location = getLocation(conn, dir);
                // name
                TextLayout layout = new TextLayout(this.createText(conn), g.getFont(), g.getFontRenderContext());
                Rectangle bounds = layout.getBounds().getBounds();
                int width = bounds.width;
                paintText(conn, g, layout, location.first, width, dir);
                paintText(conn, g, layout, location.second, width, dir);
                // line
                paintLine(conn, g, location.first, width, true, dir);
                paintLine(conn, g, location.second, width, false, dir);
            }
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

    private void paintText(FNConnection conn, Graphics2D g, TextLayout layout, Point point, int width, int dir) {
        Rectangle rectangle = layout.getBounds().getBounds();
        int dyStart = dir > 0 ? point.y - 2 * arrow : point.y + rectangle.height;
        if (dir < 0) {
            point.y += rectangle.height;
        }
        rectangle.translate(point.x - (width / 2), dyStart);
        while (collisions(rectangle)) {
            int dy = - (rectangle.height + 2 * arrow) * dir;
            rectangle.translate(0, dy);
            point.y += dy;
        }
        layout.draw(g, rectangle.x, rectangle.y + rectangle.height);
        written.add(rectangle);
        if (collector != null) {
            collector.addRegion(conn, rectangle);
        }
    }

    private void paintLine(FNConnection conn, Graphics2D g, Point point, int width, boolean left, int dir) {
        int half = (width + lineExtend) / 2;
        int x1 = point.x - half;
        int y = point.y - (arrow * dir);
        int x2 = point.x + half;
        Line2D line = new Line2D.Float(x1, y, x2, y);
        g.draw(line);
        // draw arrow
        Polygon p = new Polygon();
        if (left) {
            p.addPoint(x2 - arrow, y - arrow);
            p.addPoint(x2, y);
            p.addPoint(x2 - arrow, y + arrow);
        } else {
            p.addPoint(x1 + arrow, y - arrow);
            p.addPoint(x1, y);
            p.addPoint(x1 + arrow, y + arrow);
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
        int y = drawBase.getY(node, track);
        int x1 = drawBase.getX((conn.getFrom().getStart() + conn.getFrom().getEnd()) / 2);
        int x2 = drawBase.getX((conn.getTo().getStart() + conn.getTo().getEnd()) / 2);

        return new Tuple<Point>(
                new Point(x1, y),
                new Point(x2, y));
    }
}
