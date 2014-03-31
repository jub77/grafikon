package net.parostroj.timetable.gui.views.graph;

import java.awt.Color;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;

/**
 * Freight net component.
 *
 * @author jub
 */
public class FreightNetGraphComponent extends mxGraphComponent {

    public FreightNetGraphComponent(FreightNetGraphAdapter graph) {
        super(graph);
        this.setDoubleBuffered(false);
        this.setDragEnabled(false);
        this.getViewport().setOpaque(true);
        this.getViewport().setBackground(Color.WHITE);
        this.getConnectionHandler().setHandleEnabled(false);
        this.getConnectionHandler().setEnabled(false);
    }

    public mxGraphOutline createOutline() {
        mxGraphOutline outline = new mxGraphOutline(this) {
            {
                outlineBorder = 5;
            }
        };
        outline.setZoomHandleVisible(false);
        return outline;
    }
}
