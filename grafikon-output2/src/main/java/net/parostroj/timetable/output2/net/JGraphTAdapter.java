package net.parostroj.timetable.output2.net;

import java.util.Map;

import java.util.function.Function;
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
public abstract class JGraphTAdapter<V, E> extends mxGraph {

    private final Graph<V, E> graphT;
    private final BiMap<V, mxCell> vertexToCellMap = HashBiMap.create();
    private final BiMap<E, mxCell> edgeToCellMap = HashBiMap.create();
    private final GraphListener<V, E> graphListener;
    private final Function<Object, String> valueToString;

    protected JGraphTAdapter(final Graph<V, E> aGraphT, Function<Object, String> aValueToString) {
        valueToString = aValueToString;
        setGridEnabled(false);
        graphT = aGraphT;
        insertJGraphT(graphT);
        graphListener = new GraphListener<V, E>() {
            @Override
            public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
                addJGraphTEdge(e.getEdge());
            }

            @Override
            public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
                mxCell cell = edgeToCellMap.remove(e.getEdge());
                removeCells(new Object[]{cell});
            }

            @Override
            public void vertexAdded(GraphVertexChangeEvent<V> e) {
                addJGraphTVertex(e.getVertex());
            }

            @Override
            public void vertexRemoved(GraphVertexChangeEvent<V> e) {
                mxCell cell = vertexToCellMap.remove(e.getVertex());
                removeCells(new Object[]{cell});
            }
        };
    }

    public void listenToChanges() {
        if (graphT instanceof ListenableGraph) {
            ((ListenableGraph<V, E>) graphT).addGraphListener(graphListener);
        } else {
            throw new IllegalStateException("The graph is not listenable");
        }
    }

    @Override
    public final String convertValueToString(Object cell) {
        return valueToString.apply(cell);
    }

    private void addJGraphTVertex(V vertex) {
        getModel().beginUpdate();
        try {
            mxCell cell = this.getVertexCell(vertex);
            addCell(cell, defaultParent);
            this.updateCellSize(cell);
            vertexToCellMap.put(vertex, cell);
            updateVertexLocation(vertex, cell);
        } finally {
            getModel().endUpdate();
        }
    }

    private void addJGraphTEdge(E edge) {
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

    protected void updateVertexLocation(V vertex, mxCell cell) {}

    public Graph<V, E> getNet() {
        return graphT;
    }
}
