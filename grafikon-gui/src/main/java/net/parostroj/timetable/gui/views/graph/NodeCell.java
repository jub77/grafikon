package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeType;

import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencilRegistry;

/**
 * Cell for nodes.
 *
 * @author jub
 */
public class NodeCell extends mxCell {

    public NodeCell(Object value) {
        super(value);
    }

    public NodeShape getShape() {
        return getShapeForNode((Node) value);
    }

    @Override
    public String getStyle() {
        NodeShape shape = getShapeForNode((Node) value);
        return "shape=" + shape.getName() + ";" + super.getStyle();
    }

    private NodeShape getShapeForNode(Node vertex) {
        NodeShape shape = (NodeShape) mxStencilRegistry.getStencil(vertex.getType().getKey());
        if (shape == null) {
            // shape for station should always exist
            shape = (NodeShape) mxStencilRegistry.getStencil(NodeType.STATION.getKey());
        }
        return shape;
    }
}
