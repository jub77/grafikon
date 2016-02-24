package net.parostroj.timetable.gui.views.graph;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.model.units.UnitUtil;

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

/**
 * Specific adapter for graph with nodes and lines.
 *
 * @author jub
 */
public class NetGraphAdapter extends JGraphTAdapter<Node, Line> {

    private static final Logger log = LoggerFactory.getLogger(NetGraphAdapter.class);

    private final ApplicationModel appModel;

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
            Node node = (Node) mxCell.getValue();
            Company company = node.getAttribute(Node.ATTR_COMPANY, Company.class);
            List<Region> regions = node.getRegions();
            List<Region> centerRegions = node.getCenterRegions();
            value = node.getName();
            String description = null;
            if (company != null) {
                description = company.getAbbr();
            }
            if (!regions.isEmpty()) {
                String regionsStr = regions.stream()
                        .map(region -> centerRegions.contains(region) ?
                                String.format("<b>%s</b>", region.getName()) : region.getName())
                        .collect(Collectors.joining(","));
				description = description == null ? regionsStr : description + "," + regionsStr;
            }
            if (description != null) {
                value = String.format("%s%n(%s)", value, description);
            }
        } else {
            value = "";
        }
        return value;
    }

    @Override
    public boolean isAutoSizeCell(Object cell) {
        return true;
    }

    private String convertLine(Line line) {
        if (appModel == null) {
            return line.toString();
        }
        StringBuilder result = new StringBuilder();
        collectRoutes(line, result);
        if (result.length() != 0) {
            result.append('\n');
        }
        LengthUnit lengthUnit = line.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class, appModel.getProgramSettings().getLengthUnit());
        BigDecimal cValue = lengthUnit.convertFrom(new BigDecimal(line.getLength()), LengthUnit.MM);
        result.append(UnitUtil.convertToString("#0.###", cValue)).append(lengthUnit.getUnitsOfString());
        Integer topSpeed = line.getTopSpeed();
        if (topSpeed != null) {
            SpeedUnit speedUnit = line.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_SPEED_UNIT, SpeedUnit.class, appModel.getProgramSettings().getSpeedUnit());
            BigDecimal sValue = speedUnit.convertFrom(new BigDecimal(topSpeed), SpeedUnit.KMPH);
            result.append('(').append(UnitUtil.convertToString("#0", sValue)).append(speedUnit.getUnitsOfString()).append(')');
        }
        return result.toString();
    }

    private void collectRoutes(Line line, StringBuilder builder) {
        TrainDiagram diagram = line.getDiagram();
        boolean added = false;
        for (Route route : diagram.getRoutes()) {
            if (route.isNetPart()) {
                for (RouteSegment seg : route.getSegments()) {
                    if (seg.asLine() != null && seg.asLine() == line) {
                        if (added) {
                            builder.append(',');
                        }
                        added = true;
                        builder.append(route.getName());
                    }
                }
            }
        }
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
