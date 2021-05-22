package net.parostroj.timetable.output2.net;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
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

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.util.OutputFreightUtil;

/**
 * Specific adapter for graph with nodes and lines.
 *
 * @author jub
 */
public class NetGraphAdapter extends JGraphTAdapter<Node, Line> {

    private static final Logger log = LoggerFactory.getLogger(NetGraphAdapter.class);

    private final NetItemConversionUtil conv;

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

    private final Supplier<LengthUnit> lengthUnit;
    private final Supplier<SpeedUnit> speedUnit;

    public NetGraphAdapter(ListenableGraph<Node, Line> graphT, Supplier<LengthUnit> lengthUnit,
            Supplier<SpeedUnit> speedUnit, boolean listenToChanges) {
        super(graphT, listenToChanges);
        this.lengthUnit = lengthUnit;
        this.speedUnit = speedUnit;
        conv = new NetItemConversionUtil();
        this.refresh();
        this.setHtmlLabels(true);
        this.initializeNodeLocations();
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

    protected String convertNode(Node node) {
        StringBuilder value = new StringBuilder();
        Company company = node.getCompany();
        List<Region> regions = OutputFreightUtil.sortRegions(node.getRegions(), Locale.getDefault());
        Set<Region> centerRegions = node.getCenterRegions();
        addTextWithColor(value, "black", node.getName());
        if (company != null) {
            value.append(" ");
            addTextWithColor(value, "gray", "[" + company.getAbbr() + "]");
        }
        if (!regions.isEmpty()) {
            String regionsStr = regions.stream()
                    .map(region -> centerRegions.contains(region) ?
                            String.format(getBoldFormat(), region.getName()) : region.getName())
                    .collect(Collectors.joining(","));
            value.append("\n(").append(regionsStr).append(')');
        }
        Collection<FreightColor> colors = OutputFreightUtil.sortFreightColors(node.getFreightColors());
        if (!colors.isEmpty()) {
            String colorsStr = colors.stream().map(FreightColor::getName).collect(Collectors.joining(",", "[", "]"));
            value.append("\n");
            addTextWithColor(value, "gray", colorsStr);
        }
        return value.toString();
    }

    protected String convertLine(Line line) {
        if (speedUnit == null || lengthUnit == null) {
            return line.toString();
        }
        StringBuilder result = new StringBuilder(conv.collectRoutesString(line));
        if (result.length() != 0) {
            result.append('\n');
        }
        result.append(conv.getLineLengthString(line, lengthUnit.get()));
        Integer topSpeed = line.getTopSpeed();
        if (topSpeed != null) {
            result.append(" (").append(conv.getLineSpeedString(line, speedUnit.get())).append(')');
        }
        return result.toString();
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

    private void initializeNodeLocations() {
        Map<Node, mxCell> map = getVertexToCellMap();
        this.getModel().beginUpdate();
        try {
            map.forEach((node, cell) -> {
                mxGeometry geometry = cell.getGeometry();
                this.moveCells(new Object[] { cell },
                        node.getLocation().getX() - geometry.getX(),
                        node.getLocation().getY() - geometry.getY());
            });
        } finally {
            this.getModel().endUpdate();
        }
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

    private void addTextWithColor(StringBuilder builder, String color, String text) {
        if (isHtmlLabels()) {
            builder.append("<font color=").append(color).append(">");
        }
        builder.append(text);
        if (isHtmlLabels()) {
            builder.append("</font>");
        }
    }

    private String getBoldFormat() {
        return isHtmlLabels() ? "<b>%s</b>" : "%s";
    }
}
