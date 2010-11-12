package net.parostroj.timetable.gui.views;

import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;

/**
 * Handling of mouse - popup menu.
 *
 * @author jub
 */
public class NetViewMarqueeHandler extends BasicMarqueeHandler {

//    private JGraph graph;

    public NetViewMarqueeHandler() {
    }

    public void setGraph(JGraph graph) {
//        this.graph = graph;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (SwingUtilities.isRightMouseButton(e)) {
            // no implementation yet
        }
    }

    @Override
    public boolean isForceMarqueeEvent(MouseEvent event) {
        if (SwingUtilities.isRightMouseButton(event))
            return true;
        return super.isForceMarqueeEvent(event);
    }
}
