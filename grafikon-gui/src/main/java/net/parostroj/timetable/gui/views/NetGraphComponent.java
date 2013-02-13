package net.parostroj.timetable.gui.views;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;

import org.jgrapht.Graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;

/**
 * Custom graph component for net.
 *
 * @author cz2b10k5
 */
public class NetGraphComponent extends mxGraphComponent {

	public NetGraphComponent(NetGraphAdapter graph) {
		super(graph);
	}

	@Override
	protected mxConnectionHandler createConnectionHandler() {
		return new mxConnectionHandler(this) {
			@Override
			public boolean isValidTarget(Object cell) {
				if (cell != null) {
					Object object = ((mxCell) cell).getValue();
					if (object instanceof Node) {
						Graph<Node, Line> net = getNet();
						Node targetNode = (Node) object;
						Node sourceNode = (Node) ((mxCell) source.getCell()).getValue();
						return !net.containsEdge(sourceNode, targetNode);
					}
				}
				return super.isValidTarget(cell);
			}
		};
	}

	protected Graph<Node, Line> getNet() {
		return ((NetGraphAdapter) getGraph()).getNet();
	}
}
