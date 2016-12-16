package net.parostroj.timetable.gui.views.graph;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.ListenableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxStencilRegistry;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.utils.NetItemConversionUtil;
import net.parostroj.timetable.model.*;

/**
 * Specific adapter for graph with nodes and lines.
 *
 * @author jub
 */
public class NetGraphAdapter extends JGraphTAdapter<Node, Line> {

    private static final Logger log = LoggerFactory.getLogger(NetGraphAdapter.class);

    private final ApplicationModel appModel;
    private final NetItemConversionUtil conv;

    static {
        try {
            InputStream is = NetGraphAdapter.class.getResourceAsStream("/graph/shapes.xml");
            Document doc = mxXmlUtils.parseXml(mxUtils.readInputStream(is));
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

    public NetGraphAdapter(ListenableGraph<Node, Line> graphT, ApplicationModel model) {
        super(graphT);
        conv = new NetItemConversionUtil();
        appModel = model;
        this.refresh();
        this.setHtmlLabels(true);
    }

    @Override
    public String convertValueToString(Object cell) {
        mxCell mxCell = (mxCell) cell;
        String value;
        if (mxCell.getValue() instanceof Line) {
            value = this.convertLine((Line) mxCell.getValue());
        } else if (mxCell.getValue() instanceof Node) {
            value = this.convertNode((Node) mxCell.getValue());
        } else {
            value = "";
        }
        return value;
    }

    @Override
    public boolean isAutoSizeCell(Object cell) {
        return true;
    }

    private String convertNode(Node node) {
        StringBuilder value = new StringBuilder();
        Company company = node.getCompany();
        Set<Region> regions = node.getRegions();
        Set<Region> centerRegions = node.getCenterRegions();
        value.append("<font color=black>").append(node.getName()).append("</font>");
        if (company != null) {
            value.append(" <font color=gray>[").append(company.getAbbr()).append("]</font>");
        }
        if (!regions.isEmpty()) {
            String regionsStr = regions.stream()
                    .map(region -> centerRegions.contains(region) ?
                            String.format("<b>%s</b>", region.getName()) : region.getName())
                    .collect(Collectors.joining(","));
            value = value.append("\n(").append(regionsStr).append(')');
        }
        Collection<FreightColor> colors = node.getSortedFreightColors();
        if (!colors.isEmpty()) {
            String colorsStr = colors.stream().map(color -> color.getName()).collect(Collectors.joining(","));
            value.append("\n<font color=gray>[").append(colorsStr).append("]</font>");
        }
        return value.toString();
    }

    private String convertLine(Line line) {
        if (appModel == null) {
            return line.toString();
        }
        StringBuilder result = new StringBuilder(conv.collectRoutesString(line));
        if (result.length() != 0) {
            result.append('\n');
        }
        result.append(conv.getLineLengthString(line, appModel.getProgramSettings().getLengthUnit()));
        Integer topSpeed = line.getTopSpeed();
        if (topSpeed != null) {
            result.append('(').append(conv.getLineSpeedString(line, appModel.getProgramSettings().getSpeedUnit())).append(')');
        }
        return result.toString();
    }

    @Override
    public mxRectangle getPreferredSizeForCell(Object cell) {
        mxRectangle result = null;
        if (cell instanceof NodeCell) {
            // compute size of rectangle relative to some predefined width
            // (height?)
            NodeShape shape = ((NodeCell) cell).getShape();
            if (shape != null) {
                result = new mxRectangle(0, 0, shape.getWidth() / 2, shape.getHeight() / 2);
            } else {
                result = new mxRectangle(0, 0, 100, 100);
            }
        } else {
            result = super.getPreferredSizeForCell(cell);
        }
        return result;
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
