package net.parostroj.timetable.gui.views.graph;

import java.util.Collection;
import java.util.Collections;

import net.parostroj.timetable.model.FreightNet.FreightNetConnection;
import net.parostroj.timetable.model.FreightNet.FreightNetNode;

import org.jgrapht.ListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraphSelectionModel;

/**
 * Freight net graph adapter.
 *
 * @author jub
 */
public class FreightNetGraphAdapter extends JGraphTAdapter<FreightNetNode, FreightNetConnection> {

    static class Node extends mxCell {
        public Node(FreightNetNode node) {
            super(node);
        }

        @Override
        public String getStyle() {
            return "spacing=4;align=center;verticalAlign=middle";
        }
    }

    static class Connection extends mxCell {
        public Connection(FreightNetConnection conn) {
            super(conn);
        }
    }

    private final SelectionListener connListener;

    public interface SelectionListener {
        public void selectedConnections(Collection<FreightNetConnection> connections);
    }

    public FreightNetGraphAdapter(ListenableGraph<FreightNetNode, FreightNetConnection> graph, SelectionListener listener) {
        super(graph);
        this.connListener = listener;
        this.setConnectableEdges(false);
        this.setAllowDanglingEdges(false);
        this.setEdgeLabelsMovable(false);
        this.setDisconnectOnMove(false);
        this.setAutoSizeCells(true);
        this.setDropEnabled(false);
        this.setAllowNegativeCoordinates(false);
        this.setCellsResizable(false);
        this.setCellsCloneable(false);
        this.setCellsEditable(false);
        this.setGridEnabled(false);
        this.setCellsDisconnectable(false);
        this.setDisconnectOnMove(false);

        this.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
                mxGraphSelectionModel mm = (mxGraphSelectionModel) sender;
                mxCell cell = (mxCell) mm.getCell();
                if (cell != null && mm.getCells().length == 1 && cell.getValue() instanceof FreightNetConnection) {
                    connListener.selectedConnections(Collections.singletonList((FreightNetConnection) cell.getValue()));
                } else {
                    connListener.selectedConnections(Collections.<FreightNetConnection>emptyList());
                }
            }
        });
    }

    @Override
    protected mxCell getVertexCell(FreightNetNode vertex) {
        mxCell cell = new Node(vertex);
        cell.setVertex(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        return cell;
    }

    @Override
    protected mxCell getEdgeCell(FreightNetConnection edge) {
        mxCell cell = new Connection(edge);
        cell.setEdge(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        cell.getGeometry().setRelative(true);
        return cell;
    }
}
