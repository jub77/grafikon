package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.util.Collection;

import net.parostroj.timetable.model.*;

public class ManagedFreightGTDraw extends GTDrawDecorator {

    private static final Stroke s = new BasicStroke(2);

    private GTDrawBase drawBase;

    public ManagedFreightGTDraw(GTDraw draw) {
        super(draw);
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
        for (FNConnection con : connections) {
            if (drawBase.positions.containsKey(con.getFrom().getOwnerAsNode())) {
                int y = drawBase.getY(con.getFrom().getOwnerAsNode());
                int x1 = drawBase.getX(con.getFrom().getStart());
                int x2 = drawBase.getX(con.getTo().getEnd());
                g.drawLine(x1, y, x2, y);
            }
        }
        super.draw(g);
    }
}
