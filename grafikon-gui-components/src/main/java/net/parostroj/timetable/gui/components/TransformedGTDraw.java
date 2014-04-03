package net.parostroj.timetable.gui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.Train;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GTDraw with translation and clipping.
 *
 * @author jub
 */
public class TransformedGTDraw implements GTDraw {

    private static final Logger LOG = LoggerFactory.getLogger(TransformedGTDraw.class.getName());
    private final GTDraw draw;
    private Rectangle clipping;
    private boolean moveStationName;

    public TransformedGTDraw(GTDraw draw) {
        this.draw = draw;
    }
    private static int drawCnt = 0;

    public void draw(Graphics2D g) {
        long time = System.currentTimeMillis();
        this.applyTranslation(g);
        this.applyClipping(g);
        this.draw.draw(g);
        LOG.trace("DRAW TIME(" + (drawCnt++) + "): " + (System.currentTimeMillis() - time) + "ms");
    }

    private void applyClipping(Graphics2D g) {
        if (clipping != null) {
            g.setClip(clipping.x, clipping.y, clipping.width, clipping.height);
        }
    }

    private void applyTranslation(Graphics2D g) {
        if (clipping != null) {
            AffineTransform transform = g.getTransform();
            transform.translate(-clipping.x, -clipping.y);
            g.setTransform(transform);
            if (moveStationName) {
                draw.setPositionX(clipping.x);
            } else {
                draw.setPositionX(0);
            }
        }
    }

    public void setClipping(Rectangle rectangle) {
        clipping = rectangle;
    }

    public void setMoveStationNames(boolean move) {
        moveStationName = move;
    }

    @Override
    public void paintStationNames(Graphics g) {
        draw.paintStationNames(g);
    }

    @Override
    public Route getRoute() {
        return draw.getRoute();
    }

    @Override
    public void setPositionX(int positionX) {
        draw.setPositionX(positionX);
    }

    @Override
    public float getFontSize() {
        return draw.getFontSize();
    }

    @Override
    public void removedTrain(Train train) {
        draw.removedTrain(train);
    }

    @Override
    public void changedTextTrain(Train train) {
        draw.changedTextTrain(train);
    }

    @Override
    public void changedTextNode(Node node) {
        draw.changedTextNode(node);
    }

    @Override
    public void changedTextAllTrains() {
        draw.changedTextAllTrains();
    }
}
