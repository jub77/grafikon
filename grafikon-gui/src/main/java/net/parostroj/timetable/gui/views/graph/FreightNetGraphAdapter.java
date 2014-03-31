package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.model.FreightNet.FreightNetConnection;
import net.parostroj.timetable.model.FreightNet.FreightNetNode;

import org.jgrapht.ListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

/**
 * Freight net graph adapter.
 *
 * @author jub
 */
public class FreightNetGraphAdapter extends JGraphTAdapter<FreightNetNode, FreightNetConnection> {

    public FreightNetGraphAdapter(ListenableGraph<FreightNetNode, FreightNetConnection> graph) {
        super(graph);
        this.setConnectableEdges(false);
        this.setAllowDanglingEdges(false);
        this.setEdgeLabelsMovable(false);
        this.setDisconnectOnMove(false);
        this.setAutoSizeCells(true);
        this.setDropEnabled(false);
        this.setAllowNegativeCoordinates(false);
        this.setCellsResizable(false);
        this.setCellsCloneable(false);
        this.setGridEnabled(false);
        this.setCellsDisconnectable(false);
        this.setDisconnectOnMove(false);
    }

    @Override
    protected mxCell getVertexCell(FreightNetNode vertex) {
        mxCell cell = new mxCell(vertex);
        cell.setVertex(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        cell.setStyle("spacing=4;align=center;verticalAlign=middle");
        return cell;
    }

    @Override
    protected mxCell getEdgeCell(FreightNetConnection edge) {
        mxCell cell = new mxCell(edge);
        cell.setEdge(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        cell.getGeometry().setRelative(true);
        return cell;
    }
}
