/*
 * NetView.java
 *
 * Created on 24. srpen 2007, 9:09
 */
package net.parostroj.timetable.gui.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.RouteSegment;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListener;

import org.jgrapht.ListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

/**
 * Net view ....
 *
 * @author jub
 */
public class NetView extends javax.swing.JPanel implements ApplicationModelListener, mxIEventListener, TrainDiagramListener {

    private ApplicationModel model;
    private JGraphXAdapter<Node, Line> mxGraph;
    private mxGraphComponent mxGraphComponent;

    public NetView() {
        setLayout(new java.awt.BorderLayout());
    }

    public void setGraphCallbacks(mxIEventListener selection) {
    	// TODO call always ...
    	if (mxGraph != null)
    		mxGraph.addListener(mxEvent.SELECT, selection);
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
        this.model.addListener(this);
        this.setNet(model);
    }

    public Dimension getGraphSize() {
        return mxGraphComponent.getPreferredSize();
    }

    public void paintGraph(Graphics g) {
        mxGraphComponent.paint(g);
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        switch (event.getType()) {
        case SET_DIAGRAM_CHANGED:
            if (model.getDiagram() != null) {
                this.setNet(model);
                model.getDiagram().addListener(this);
            }
            break;
        case MODIFIED_NODE:
            // redraw node
            this.updateNode((Node)event.getObject());
            break;
        case MODIFIED_LINE:
            // redraw line
            this.updateLine((Line)event.getObject());
            break;
        default:
            break;
        }
    }

    @Override
    public void invoke(Object sender, mxEventObject evt) {
    	System.out.println("---------sender----------: " + evt.getProperties());
    	// TODO write back positions ...
//        node.setPositionX(b.getBounds().x);
//        node.setPositionY(b.getBounds().y);
    }

    private void setNet(ApplicationModel model) {
        if (model.getDiagram() != null) {
            this.setNet(model.getDiagram().getNet());
        } else {
            this.setNet((Net)null);
        }
    }

    private void setNet(Net net) {
    	if (mxGraphComponent != null)
    		this.remove(mxGraphComponent);
        mxGraphComponent = null;
        mxGraph = null;
        if (net == null)
            return;

        mxGraph = new JGraphXAdapter<Node, Line>((ListenableGraph<Node, Line>) net.getGraph());
        mxGraphComponent = new mxGraphComponent(mxGraph);
        this.add(mxGraphComponent);
        mxGraph.setCellsEditable(false);
        mxGraph.setConnectableEdges(false);
        mxGraph.setEdgeLabelsMovable(false);
        mxGraph.getSelectionModel().setSingleSelection(true);
        mxGraph.setDisconnectOnMove(false);
        mxGraph.setAutoSizeCells(true);
        mxGraph.setDropEnabled(false);
        mxGraph.setAllowNegativeCoordinates(false);
        mxGraphComponent.setDoubleBuffered(false);
        mxGraphComponent.setDragEnabled(false);
        mxGraphComponent.getViewport().setOpaque(true);
        mxGraphComponent.getViewport().setBackground(Color.WHITE);

//        // initialize adapter
//        netAdapter = new JGraphModelAdapter<Node, Line>(net.getGraph(),
//                JGraphModelAdapter.createDefaultVertexAttributes(),
//                JGraphModelAdapter.createDefaultEdgeAttributes(net.getGraph()),
//                new JGraphModelAdapter.DefaultCellFactory<Node, Line>() {
//
//                    @Override
//                    public DefaultEdge createEdgeCell(Line line) {
//                        return new DefaultEdge(line) {
//
//                            @Override
//                            public String toString() {
//                                StringBuilder result = new StringBuilder();
//                                Line line = (Line)getUserObject();
//                                collectRoutes(line, result);
//                                if (result.length() != 0)
//                                    result.append(';');
//                                LengthUnit lengthUnit = model.getProgramSettings().getLengthUnit();
//                                BigDecimal cValue = lengthUnit.convertFrom(new BigDecimal(line.getLength()), LengthUnit.MM);
//                                result.append(UnitUtil.convertToString("#0.###", cValue)).append(lengthUnit.getUnitsOfString());
//                                int topSpeed = line.getTopSpeed();
//                                if (topSpeed != Line.UNLIMITED_SPEED) {
//                                    lengthUnit = model.getProgramSettings().getSpeedLengthUnit();
//                                    BigDecimal sValue = lengthUnit.convertFrom(new BigDecimal(topSpeed), LengthUnit.KM);
//                                    result.append(';').append(UnitUtil.convertToString("#0", sValue)).append(lengthUnit.getUnitsOfString()).append("/h");
//                                }
//                                return result.toString();
//                            }
//                        };
//                    }
//
//                    private void collectRoutes(Line line, StringBuilder builder) {
//                        TrainDiagram diagram = line.getTrainDiagram();
//                        boolean added = false;
//                        for (Route route : diagram.getRoutes()) {
//                            if (route.isNetPart()) {
//                                for (RouteSegment seg : route.getSegments()) {
//                                    if (seg.asLine() != null && seg.asLine() == line) {
//                                        if (added)
//                                            builder.append(',');
//                                        added = true;
//                                        builder.append(route.getName());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//        mxGraph.setModel(netAdapter);

        mxGraph.addListener(mxEvent.CELLS_MOVED, this);
        mxGraph.getModel().beginUpdate();
        for (Node node : net.getNodes()) {
        	mxCell cell = mxGraph.getVertexToCellMap().get(node);
			mxGraph.getModel().setGeometry(cell, new mxGeometry(node.getPositionX(), node.getPositionY(), 10, 10));
        }
        mxGraph.getModel().endUpdate();
    }

    private void updateNode(Node node) {
    	// TODO check if it is working - label change ..
        mxGraph.getModel().beginUpdate();
        mxGraph.cellLabelChanged(mxGraph.getVertexToCellMap().get(node), null, true);
        mxGraph.getModel().endUpdate();
    }

    private void updateLine(Line line) {
    	// TODO check if it is working - label change ..
        mxGraph.getModel().beginUpdate();
        mxGraph.cellLabelChanged(mxGraph.getEdgeToCellMap().get(line), null, true);
        mxGraph.getModel().endUpdate();
    }

    @Override
    public void trainDiagramChanged(TrainDiagramEvent event) {
        switch (event.getType()) {
            case ROUTE_ADDED:
            case ROUTE_REMOVED:
                Route route = (Route)event.getObject();
                for (RouteSegment seg : route.getSegments()) {
                    if (seg.asLine() != null) {
                        this.updateLine(seg.asLine());
                    }
                }
            default:
                break;
        }
    }
}
