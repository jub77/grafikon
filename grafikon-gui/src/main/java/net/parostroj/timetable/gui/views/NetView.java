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
import net.parostroj.timetable.model.*;
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
	private mxIEventListener selectionListener;

    public NetView() {
        setLayout(new java.awt.BorderLayout());
    }

    public void setGraphCallbacks(mxIEventListener selection) {
    	this.selectionListener = selection;
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
    	Object[] cells = (Object[]) evt.getProperty("cells");
    	if (cells != null) {
    		for (Object cell : cells) {
    			mxCell mxCell = (mxCell) cell;
    			if (mxCell.getValue() instanceof Node) {
	    			Node node = (Node) mxCell.getValue();
	    			node.setPositionX((int) (node.getPositionX() + (Double) evt.getProperty("dx")));
	    			node.setPositionY((int) (node.getPositionY() + (Double) evt.getProperty("dy")));
    			}
    		}
    	}
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

        mxGraph = new NodeLineGraphAdapter((ListenableGraph<Node, Line>) net.getGraph(), model);
        mxGraphComponent = new mxGraphComponent(mxGraph);
        this.add(mxGraphComponent);
        mxGraph.setCellsEditable(false);
        mxGraph.setConnectableEdges(false);
        mxGraph.setAllowDanglingEdges(false);
        mxGraph.setEdgeLabelsMovable(false);
        mxGraph.getSelectionModel().setSingleSelection(true);
        mxGraph.setDisconnectOnMove(false);
        mxGraph.setAutoSizeCells(true);
        mxGraph.setDropEnabled(false);
        mxGraph.setAllowNegativeCoordinates(false);
        mxGraph.setCellsResizable(false);
        mxGraph.getSelectionModel().addListener(mxEvent.CHANGE, selectionListener);
        mxGraphComponent.setDoubleBuffered(false);
        mxGraphComponent.setDragEnabled(false);
        mxGraphComponent.getViewport().setOpaque(true);
        mxGraphComponent.getViewport().setBackground(Color.WHITE);

        mxGraph.addListener(mxEvent.CELLS_MOVED, this);
        mxGraph.getModel().beginUpdate();
        try {
	        for (Node node : net.getNodes()) {
	        	mxCell cell = mxGraph.getVertexToCellMap().get(node);
				mxGraph.getModel().setGeometry(cell, new mxGeometry(node.getPositionX(), node.getPositionY(), 0, 0));
				mxGraph.updateCellSize(cell);
	        }
        } finally {
        	mxGraph.getModel().endUpdate();
        }
    }

    private void updateNode(Node node) {
    	mxGraph.cellLabelChanged(mxGraph.getVertexToCellMap().get(node), node, true);
    }

    private void updateLine(Line line) {
    	mxGraph.cellLabelChanged(mxGraph.getEdgeToCellMap().get(line), line, true);
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
