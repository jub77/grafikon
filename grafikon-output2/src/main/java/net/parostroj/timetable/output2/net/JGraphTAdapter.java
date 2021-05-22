package net.parostroj.timetable.output2.net;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * Adapter for jgrapht graph to mxGraph.
 */
public abstract class JGraphTAdapter<V, E> extends mxGraph implements GraphListener<V, E> {

    private final ListenableGraph<V, E> graphT;
    private final BiMap<V, mxCell> vertexToCellMap = HashBiMap.create();
    private final BiMap<E, mxCell> edgeToCellMap = HashBiMap.create();

    protected JGraphTAdapter(final ListenableGraph<V, E> graphT) {
        super();
        this.setGridEnabled(false);
        this.graphT = graphT;
        graphT.addGraphListener(this);
        insertJGraphT(graphT);
    }

    public void addJGraphTVertex(V vertex) {
        getModel().beginUpdate();
        try {
            mxCell cell = this.getVertexCell(vertex);
            addCell(cell, defaultParent);
            this.updateCellSize(cell);
            vertexToCellMap.put(vertex, cell);
        } finally {
            getModel().endUpdate();
        }
    }

    public void addJGraphTEdge(E edge) {
        getModel().beginUpdate();
        try {
            V source = graphT.getEdgeSource(edge);
            V target = graphT.getEdgeTarget(edge);
            mxCell cell = this.getEdgeCell(edge);
            addEdge(cell, defaultParent, vertexToCellMap.get(source), vertexToCellMap.get(target), null);
            edgeToCellMap.put(edge, cell);
        } finally {
            getModel().endUpdate();
        }
    }

    public Map<V, mxCell> getVertexToCellMap() {
        return vertexToCellMap;
    }

    public Map<E, mxCell> getEdgeToCellMap() {
        return edgeToCellMap;
    }

    public Map<mxCell, E> getCellToEdgeMap() {
        return edgeToCellMap.inverse();
    }

    public Map<mxCell, V> getCellToVertexMap() {
        return vertexToCellMap.inverse();
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> e) {
        addJGraphTVertex(e.getVertex());
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> e) {
        mxCell cell = vertexToCellMap.remove(e.getVertex());
        removeCells(new Object[] { cell });
    }

    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
        addJGraphTEdge(e.getEdge());
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
        mxCell cell = edgeToCellMap.remove(e.getEdge());
        removeCells(new Object[] { cell });
    }

    private void insertJGraphT(Graph<V, E> graphT) {
        getModel().beginUpdate();
        try {
            for (V vertex : graphT.vertexSet())
                addJGraphTVertex(vertex);
            for (E edge : graphT.edgeSet())
                addJGraphTEdge(edge);
        } finally {
            getModel().endUpdate();
        }
    }

    protected abstract mxCell getVertexCell(V vertex);

    protected abstract mxCell getEdgeCell(E edge);

    /**
     * @return net
     */
    public Graph<V, E> getNet() {
        return graphT;
    }
}
