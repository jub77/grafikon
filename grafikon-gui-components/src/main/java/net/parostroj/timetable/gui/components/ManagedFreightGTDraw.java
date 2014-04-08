package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Collection;

import net.parostroj.timetable.model.*;

public class ManagedFreightGTDraw extends GTDrawDecorator {

    private static final Stroke s = new BasicStroke(2);

    private GTDrawBase drawBase;
    private final RegionCollector<FNConnection> collector;

    public ManagedFreightGTDraw(GTDraw draw, RegionCollector<FNConnection> collector) {
        super(draw);
        this.collector = collector;
        if (draw instanceof GTDrawBase) {
            drawBase = (GTDrawBase) draw;
        } else {
            throw new IllegalArgumentException("Has to be instance of base gt draw.");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // draw managed trains ...
        g.setColor(Color.magenta);
        g.setStroke(s);

        Route route = drawBase.getRoute();
        RouteSegment fSegment = route.getSegments().get(0);
        FreightNet net = fSegment.asNode().getTrainDiagram().getFreightNet();

        Collection<FNConnection> connections = net.getConnections();
        drawBase.init(g);
        if (collector != null) {
            collector.clear();
        }
        for (FNConnection con : connections) {
            if (drawBase.positions.containsKey(con.getFrom().getOwnerAsNode())) {
                int y = drawBase.getY(con.getFrom().getOwnerAsNode());
                int x1 = drawBase.getX(con.getFrom().getStart());
                int x2 = drawBase.getX(con.getTo().getEnd());
                Line2D line = new Line2D.Float(x1, y, x2, y);
                g.draw(line);
                g.drawLine(x1, y, x2, y);
                if (collector != null) {
                    collector.addRegion(con, line);
                }
            }
        }
        super.draw(g);
    }
}
