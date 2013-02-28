package net.parostroj.timetable.gui.views;

import com.mxgraph.model.mxCell;

/**
 * Cell for nodes.
 *
 * @author cz2b10k5
 */
public class NodeCell extends mxCell {

	private NodeShape shape;

	public NodeCell(Object value) {
		super(value);
	}

	public NodeShape getShape() {
		return shape;
	}

	public void setShape(NodeShape shape) {
		this.shape = shape;
	}
}
