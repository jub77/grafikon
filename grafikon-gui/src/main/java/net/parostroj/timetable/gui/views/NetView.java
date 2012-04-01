/*
 * NetView.java
 *
 * Created on 24. srpen 2007, 9:09
 */
package net.parostroj.timetable.gui.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListener;
import org.jgraph.event.GraphModelEvent;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgraph.JGraph;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.*;
import org.jgraph.plaf.basic.BasicGraphUI;

/**
 * Net view ....
 * 
 * @author jub
 */
public class NetView extends javax.swing.JPanel implements ApplicationModelListener, GraphModelListener, TrainDiagramListener {

    private JGraphModelAdapter<Node, Line> netAdapter;
    private ApplicationModel model;
    private JGraph jGraph;

    public NetView() {
        initComponents();
        GraphModel m = new DefaultGraphModel();
        jGraph = new JGraph(m);
        this.add(jGraph);
        jGraph.setEditable(false);
        jGraph.setConnectable(false);
        jGraph.setSizeable(false);
        jGraph.setDisconnectable(false);
        jGraph.setEdgeLabelsMovable(false);
        jGraph.getSelectionModel().setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
    }

    public void setGraphCallbacks(GraphSelectionListener listener, BasicMarqueeHandler handler) {
        jGraph.addGraphSelectionListener(listener);
        jGraph.setMarqueeHandler(handler);
    }
    
    public void setModel(ApplicationModel model) {
        this.model = model;
        this.model.addListener(this);
        this.setNet(model);
    }

    public Dimension getGraphSize() {
        return jGraph.getPreferredScrollableViewportSize();
    }

    public void paintGraph(Graphics g) {
        ((BasicGraphUI)jGraph.getUI()).drawGraph(g, null);
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
        }
    }

    @Override
    public void graphChanged(GraphModelEvent event) {
        for (Object o : event.getChange().getChanged()) {
            if (!(o instanceof DefaultGraphCell))
                continue;
            DefaultGraphCell cell = (DefaultGraphCell)o;
            if (!(cell.getUserObject() instanceof Node))
                continue;
            Node node = (Node)cell.getUserObject();
            if (node == null)
                continue;
            Map<?,?> attr = cell.getAttributes();
            Rectangle2D b = GraphConstants.getBounds(attr);
            if (!callChange) {
                node.setPositionX(b.getBounds().x);
                node.setPositionY(b.getBounds().y);
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
        if (net == null) {
            jGraph.setModel(new DefaultGraphModel());
            return;
        }

        // remove listener
        jGraph.getModel().removeGraphModelListener(this);
        jGraph.getSelectionModel().clearSelection();
        
        // initialize adapter
        netAdapter = new JGraphModelAdapter<Node, Line>(net.getGraph(),
                JGraphModelAdapter.createDefaultVertexAttributes(),
                JGraphModelAdapter.createDefaultEdgeAttributes(net.getGraph()),
                new JGraphModelAdapter.DefaultCellFactory<Node, Line>() {

                    @Override
                    public DefaultEdge createEdgeCell(Line line) {
                        return new DefaultEdge(line) {

                            @Override
                            public String toString() {
                                StringBuilder result = new StringBuilder();
                                Line line = (Line)getUserObject();
                                collectRoutes(line, result);
                                if (result.length() != 0)
                                    result.append(';');
                                result.append(line.getLength() / 10).append("cm");
                                int topSpeed = line.getTopSpeed();
                                if (topSpeed != Line.UNLIMITED_SPEED)
                                    result.append(';').append(topSpeed).append("km/h");
                                return result.toString();
                            }
                        };
                    }

                    private void collectRoutes(Line line, StringBuilder builder) {
                        TrainDiagram diagram = line.getTrainDiagram();
                        boolean added = false;
                        for (Route route : diagram.getRoutes()) {
                            if (route.isNetPart()) {
                                for (RouteSegment seg : route.getSegments()) {
                                    if (seg.asLine() != null && seg.asLine() == line) {
                                        if (added)
                                            builder.append(',');
                                        added = true;
                                        builder.append(route.getName());
                                    }
                                }
                            }
                        }
                    }
                });
        jGraph.setModel(netAdapter);
        
        netAdapter.beginUpdate();
        for (Node point : net.getNodes()) {
            this.positionVertexAt(point, point.getPositionX(), point.getPositionY());
        }
        netAdapter.endUpdate();

        // set listener afterwards to not receive event of initial placement
        netAdapter.addGraphModelListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private void positionVertexAt(Object vertex, int x, int y) {
        DefaultGraphCell cell = netAdapter.getVertexCell(vertex);
        Map<?,?> attr = cell.getAttributes();
        Rectangle2D b = GraphConstants.getBounds(attr);

        GraphConstants.setBounds(attr, new Rectangle(x, y, (int) b.getWidth(), (int) b.getHeight()));

        Map<GraphCell, Map<?,?>> cellAttr = new HashMap<GraphCell, Map<?,?>>();
        cellAttr.put(cell, attr);
        netAdapter.edit(cellAttr, null, null, null);
    }

    private boolean callChange = false;

    private void updateNode(Node node) {
        callChange = true;
        netAdapter.cellsChanged(new Object[]{netAdapter.getVertexCell(node)});
        callChange = false;
    }

    private void updateLine(Line line) {
        callChange = true;
        netAdapter.cellsChanged(new Object[]{netAdapter.getEdgeCell(line)});
        callChange = false;
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
        }
    }
}
