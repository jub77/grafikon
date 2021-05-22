package net.parostroj.timetable.gui.views.graph;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;

import net.parostroj.timetable.output2.net.NetGraphAdapter;
import org.jgrapht.Graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxGraphHandler;

/**
 * Custom graph component for net.
 *
 * @author jub
 */
public class NetGraphComponent extends mxGraphComponent {

    private static final long serialVersionUID = 1L;

	private final transient MouseWheelListener wheelTracker;

    public NetGraphComponent(NetGraphAdapter graph) {
        super(graph);
        wheelTracker = e -> {
            if (e.getWheelRotation() < 0) {
                zoomIn();
            } else {
                zoomOut();
            }
        };
        setWheelScrollingEnabled(false);
        addMouseWheelListener(wheelTracker);
    }

    @Override
    protected mxConnectionHandler createConnectionHandler() {
        return new mxConnectionHandler(this) {
            @Override
            public boolean isValidTarget(Object cell) {
                if (cell != null) {
                    Object object = ((mxCell) cell).getValue();
                    if (object instanceof Node) {
                        Graph<Node, Line> net = getNet();
                        Node targetNode = (Node) object;
                        Node sourceNode = (Node) ((mxCell) source.getCell()).getValue();
                        return !net.containsEdge(sourceNode, targetNode);
                    }
                }
                return super.isValidTarget(cell);
            }
        };
    }

    @Override
    public mxGraphHandler createGraphHandler() {
        return new mxGraphHandler(this) {
            @Override
            protected boolean shouldRemoveCellFromParent(Object parent, Object[] cells, MouseEvent e) {
                return false;
            }
        };
    }

    protected Graph<Node, Line> getNet() {
        return ((NetGraphAdapter) getGraph()).getNet();
    }

    public mxGraphOutline createOutline() {
        mxGraphOutline outline = new mxGraphOutline(this) {

            private static final long serialVersionUID = 1L;

			{
                outlineBorder = 5;
            }
        };
        outline.setZoomHandleVisible(false);
        outline.addMouseWheelListener(wheelTracker);
        return outline;
    }
}
