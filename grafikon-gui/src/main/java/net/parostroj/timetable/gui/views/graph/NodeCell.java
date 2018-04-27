package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeType;

import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencilRegistry;
import com.mxgraph.util.mxRectangle;

/**
 * Cell for nodes.
 *
 * @author jub
 */
public class NodeCell extends mxCell {

    private static final long serialVersionUID = 1L;

    public NodeCell(Node value) {
        super(value);
    }

    @Override
    public String getStyle() {
        Node node = (Node) value;
        StringBuilder style = new StringBuilder(super.getStyle());
        NodeShape shape = getShapeForNode(node);
        StyleHelper.nodeShape(style, shape);
        StyleHelper.colorOfNode(style, node);
        return style.toString();
    }

    public mxRectangle getPreferredSize() {
        NodeShape shape = getShapeForNode((Node) value);
        return shape != null
                ? new mxRectangle(0, 0, shape.getWidth() / 2, shape.getHeight() / 2)
                : new mxRectangle(0, 0, 100, 100);
    }

    private NodeShape getShapeForNode(Node node) {
        NodeType type = node.getType();
        NodeShape shape = type != null ? (NodeShape) mxStencilRegistry.getStencil(type.getKey()) : null;
        if (shape == null) {
            // shape for station should always exist
            shape = (NodeShape) mxStencilRegistry.getStencil(NodeType.STATION.getKey());
        }
        return shape;
    }
}
