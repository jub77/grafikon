package net.parostroj.timetable.gui.views.graph;

import java.util.Collection;
import java.util.Collections;

import net.parostroj.timetable.model.FreightNet;

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
public class FreightNetGraphAdapter extends JGraphTAdapter<FreightNet.Node, FreightNet.Connection> {

    static class Node extends mxCell {
        public Node(FreightNet.Node node) {
            super(node);
        }

        @Override
        public String getStyle() {
            return "spacing=4;align=center;verticalAlign=middle";
        }
    }

    static class Connection extends mxCell {
        public Connection(FreightNet.Connection conn) {
            super(conn);
        }
    }

    private final SelectionListener connListener;

    public interface SelectionListener {
        public void selectedConnections(Collection<FreightNet.Connection> connections);
    }

    public FreightNetGraphAdapter(ListenableGraph<FreightNet.Node, FreightNet.Connection> graph, SelectionListener listener) {
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
                if (cell != null && mm.getCells().length == 1 && cell.getValue() instanceof Connection) {
                    connListener.selectedConnections(Collections.singletonList((FreightNet.Connection) cell.getValue()));
                } else {
                    connListener.selectedConnections(Collections.<FreightNet.Connection>emptyList());
                }
            }
        });
    }

    @Override
    protected mxCell getVertexCell(FreightNet.Node vertex) {
        mxCell cell = new Node(vertex);
        cell.setVertex(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        return cell;
    }

    @Override
    protected mxCell getEdgeCell(FreightNet.Connection edge) {
        mxCell cell = new Connection(edge);
        cell.setEdge(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        cell.getGeometry().setRelative(true);
        return cell;
    }
}
