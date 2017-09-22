package net.parostroj.timetable.output2.gt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GTDraw with translation and clipping.
 *
 * @author jub
 */
public class TransformedGTDraw extends GTDrawDecorator {

    private static final Logger log = LoggerFactory.getLogger(TransformedGTDraw.class);

    private Rectangle clipping;
    public TransformedGTDraw(GTDraw draw) {
        super(draw);
    }

    private static int drawCnt = 0;

    @Override
    public void draw(Graphics2D g) {
        long time = System.currentTimeMillis();
        this.applyTranslation(g);
        this.applyClipping(g);
        super.draw(g);
        log.trace("DRAW TIME({}): {} ms", drawCnt++, System.currentTimeMillis() - time);
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
        }
    }

    public void setClipping(Rectangle rectangle) {
        clipping = rectangle;
    }
}
