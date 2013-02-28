package net.parostroj.timetable.gui.views;

import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * Adapter for jgrapht graph to mxGraph.
 */
public abstract class JGraphTAdapter<V, E> extends mxGraph implements GraphListener<V, E> {

	private ListenableGraph<V, E> graphT;
	private HashMap<V, mxCell> vertexToCellMap = new HashMap<V, mxCell>();
	private HashMap<E, mxCell> edgeToCellMap = new HashMap<E, mxCell>();
	private HashMap<mxCell, V> cellToVertexMap = new HashMap<mxCell, V>();
	private HashMap<mxCell, E> cellToEdgeMap = new HashMap<mxCell, E>();

	public JGraphTAdapter(final ListenableGraph<V, E> graphT) {
		super();
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
			cellToVertexMap.put(cell, vertex);
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
			cellToEdgeMap.put(cell, edge);
		} finally {
			getModel().endUpdate();
		}
	}

	public HashMap<V, mxCell> getVertexToCellMap() {
		return vertexToCellMap;
	}

	public HashMap<E, mxCell> getEdgeToCellMap() {
		return edgeToCellMap;
	}

	public HashMap<mxCell, E> getCellToEdgeMap() {
		return cellToEdgeMap;
	}

	public HashMap<mxCell, V> getCellToVertexMap() {
		return cellToVertexMap;
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