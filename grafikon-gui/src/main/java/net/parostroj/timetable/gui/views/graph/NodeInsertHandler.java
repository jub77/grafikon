package net.parostroj.timetable.gui.views.graph;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMouseAdapter;
import com.mxgraph.view.mxGraph;

/**
 * Handler for inserting a new node.
 *
 * @author jub
 */
public class NodeInsertHandler extends mxMouseAdapter {

    private final mxGraphComponent graphComponent;
    private boolean enabled;
    private Point started;
    private final Action insertAction;

    public NodeInsertHandler(mxGraphComponent graphComponent, Action insertAction) {
        this.graphComponent = graphComponent;
        this.insertAction = insertAction;

        graphComponent.getGraphControl().addMouseListener(this);
        graphComponent.getGraphControl().addMouseMotionListener(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void start(MouseEvent e) {
        this.started = e.getPoint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed()) {
            start(e);
            e.consume();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed() && started != null) {
            e.consume();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed() && started != null) {
            mxGraph graph = graphComponent.getGraph();
            double scale = graph.getView().getScale();
            int x = (int) (started.getX() / scale);
            int y = (int) (started.getY() / scale);

            insertAction.actionPerformed(new ActionEventWithLocation(this, 0, null, new Point(x, y)));

            e.consume();
        }

        reset();
    }

    public void reset() {
        started = null;
    }
}
