package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.model.*;

import org.jgrapht.ListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;

/**
 * Freight net graph adapter.
 *
 * @author jub
 */
public class FreightNetGraphAdapter extends JGraphTAdapter<Train, FNConnection> {

    static class FNodeCell extends mxCell {
        public FNodeCell(Train node) {
            super(node);
        }

        @Override
        public String getStyle() {
            return "spacing=5;align=center;verticalAlign=middle";
        }
    }

    static class FNConnectionCell extends mxCell {
        public FNConnectionCell(FNConnection conn) {
            super(conn);
        }
    }

    private static class MoveListener implements mxIEventListener {
        @Override
        public void invoke(Object sender, mxEventObject evt) {
            if (mxEvent.CELLS_MOVED.equals(evt.getName())) {
                Object[] cells = (Object[]) evt.getProperty("cells");
                if (cells != null) {
                    for (Object cell : cells) {
                        mxCell mxCell = (mxCell) cell;
                        if (mxCell.getValue() instanceof Train) {
                            // TODO set location
//                            Train node = (Train) mxCell.getValue();
//
//                            int x = (int) (mxCell.getGeometry().getX());
//                            int y = (int) (mxCell.getGeometry().getY());
//                            node.setLocation(new Location(x, y));
                        }
                    }
                }
            }
        }
    }

    public FreightNetGraphAdapter(ListenableGraph<Train, FNConnection> graph) {
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
        this.addListener(mxEvent.CELLS_MOVED, new MoveListener());
    }

    @Override
    protected mxCell getVertexCell(Train vertex) {
        mxCell cell = new FNodeCell(vertex);
        cell.setVertex(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        return cell;
    }

    @Override
    protected mxCell getEdgeCell(FNConnection edge) {
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
        if (mxCell.getValue() instanceof FNConnection) {
            FNConnection c = (FNConnection) mxCell.getValue();
            net.parostroj.timetable.model.Node node = c.getFrom().getOwnerAsNode();
            value = String.format("%s [%s]->[%s]", node.getName(),
                    node.getDiagram().getTimeConverter().convertIntToText(c.getFrom().getStart()),
                    node.getDiagram().getTimeConverter().convertIntToText(c.getTo().getEnd()));
        } else if (mxCell.getValue() instanceof Train) {
            TrainWrapperDelegate d = new TrainWrapperDelegate(
                    TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME_TWO_LINES, (TrainComparator) null);
            value = d.toString((mxCell.getValue()));
        } else {
            value = "";
        }
        return value;
    }
}
