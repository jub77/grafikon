package net.parostroj.timetable.gui.components;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

/**
 * GTDraw with translation and clipping.
 *
 * @author jub
 */
public class TransformedGTDraw {

    private static final Logger LOG = Logger.getLogger(TransformedGTDraw.class.getName());
    private GTDraw draw;
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
        LOG.finest("DRAW TIME(" + (drawCnt++) + "): " + (System.currentTimeMillis() - time) + "ms");
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
}
