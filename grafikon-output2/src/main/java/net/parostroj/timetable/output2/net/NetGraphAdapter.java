package net.parostroj.timetable.output2.net;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxStencilRegistry;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Specific adapter for graph with nodes and lines.
 *
 * @author jub
 */
public class NetGraphAdapter extends JGraphTAdapter<Node, Line> {

    private static final Logger log = LoggerFactory.getLogger(NetGraphAdapter.class);

    static {
        try {
            InputStream is = NetGraphAdapter.class.getResourceAsStream("/graph/shapes.xml");
            Document doc = mxXmlUtils.parseXml(mxUtils.readInputStream(Objects.requireNonNull(is)));
            Element shapes = doc.getDocumentElement();
            NodeList list = shapes.getElementsByTagName("shape");

            for (int i = 0; i < list.getLength(); i++) {
                Element shape = (Element) list.item(i);
                mxStencilRegistry.addStencil(shape.getAttribute("name"), new NodeShape(shape));
            }
        } catch (Exception e) {
            log.error("Cannot load shapes.", e);
        }
    }

    public NetGraphAdapter(Graph<Node, Line> graphT, Function<Node, String> nodeToString,
            Function<Line, String> lineToString) {
        super(graphT, cell -> {
            mxCell mxCell = (mxCell) cell;
            String value;
            if (mxCell.getValue() instanceof Line) {
                value = lineToString.apply((Line) mxCell.getValue());
            } else if (mxCell.getValue() instanceof Node) {
                value = nodeToString.apply((Node) mxCell.getValue());
            } else {
                value = "";
            }
            return value;
        });
        setHtmlLabels(true);
        this.refresh();
    }

    @Override
    public boolean isAutoSizeCell(Object cell) {
        return true;
    }

    @Override
    public mxRectangle getPreferredSizeForCell(Object cell) {
        mxRectangle result;
        if (cell instanceof NodeCell) {
            return ((NodeCell) cell).getPreferredSize();
        } else {
            result = super.getPreferredSizeForCell(cell);
        }
        return result;
    }

    public void updateNodeLocation(Node node) {
        mxCell cell = getVertexToCellMap().get(node);
        updateVertexLocation(node, cell);
    }

    @Override
    protected void updateVertexLocation(Node node, mxCell cell) {
        mxGeometry geometry = cell.getGeometry();
        this.moveCells(new Object[] { cell },
                node.getLocation().getX() - geometry.getX(),
                node.getLocation().getY() - geometry.getY());
    }

    @Override
    protected mxCell getVertexCell(Node vertex) {
        NodeCell cell = new NodeCell(vertex);
        cell.setVertex(true);
        cell.setId(null);
        cell.setStyle("shadow=1;foldable=0;verticalLabelPosition=top;verticalAlign=bottom");
        cell.setGeometry(new mxGeometry());
        return cell;
    }

    @Override
    protected mxCell getEdgeCell(Line edge) {
        mxCell cell = new LineCell(edge);
        cell.setEdge(true);
        cell.setId(null);
        cell.setGeometry(new mxGeometry());
        cell.getGeometry().setRelative(true);
        cell.setStyle("endArrow=none;startArrow=none");
        return cell;
    }
}
