package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.model.FreightNet;

import org.jgrapht.ListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

/**
 * Freight net graph adapter.
 *
 * @author jub
 */
public class FreightNetGraphAdapter extends JGraphTAdapter<FreightNet.FNNode, FreightNet.FNConnection> {

    static class FNodeCell extends mxCell {
        public FNodeCell(FreightNet.FNNode node) {
            super(node);
        }

        @Override
        public String getStyle() {
            return "spacing=5;align=center;verticalAlign=middle";
        }
    }

    static class FNConnectionCell extends mxCell {
        public FNConnectionCell(FreightNet.FNConnection conn) {
            super(conn);
        }
    }

    public FreightNetGraphAdapter(ListenableGraph<FreightNet.FNNode, FreightNet.FNConnection> graph) {
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
        this.setCellsEditable(false);
        this.setCellsDisconnectable(false);
        this.setDisconnectOnMove(false);
    }

    @Override
    protected mxCell getVertexCell(FreightNet.FNNode vertex) {
        mxCell cell = new FNodeCell(vertex);
        cell.setVertex(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        return cell;
    }

    @Override
    protected mxCell getEdgeCell(FreightNet.FNConnection edge) {
        mxCell cell = new FNConnectionCell(edge);
        cell.setEdge(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        cell.getGeometry().setRelative(true);
        return cell;
    }

    @Override
    public boolean isAutoSizeCell(Object cell) {
        return true;
    }

    @Override
    public String convertValueToString(Object cell) {
        mxCell mxCell = (mxCell) cell;
        String value;
        if (mxCell.getValue() instanceof FreightNet.FNConnection) {
            FreightNet.FNConnection c = (FreightNet.FNConnection) mxCell.getValue();
            net.parostroj.timetable.model.Node node = c.getFrom().getOwnerAsNode();
            value = String.format("%s [%s]->[%s]", node.getName(),
                    node.getTrainDiagram().getTimeConverter().convertIntToText(c.getFrom().getStart()),
                    node.getTrainDiagram().getTimeConverter().convertIntToText(c.getTo().getEnd()));
        } else if (mxCell.getValue() instanceof FreightNet.FNNode) {
            TrainWrapperDelegate d = new TrainWrapperDelegate(
                    TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME_TWO_LINES, (TrainComparator) null);
            value = d.toString(((FreightNet.FNNode) mxCell.getValue()).getTrain());
        } else {
            value = "";
        }
        return value;
    }
}
