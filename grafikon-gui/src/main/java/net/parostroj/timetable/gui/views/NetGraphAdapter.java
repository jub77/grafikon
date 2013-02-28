package net.parostroj.timetable.gui.views;

import java.math.BigDecimal;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
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
 * @author cz2b10k5
 */
public class NetGraphAdapter extends JGraphTAdapter<Node, Line> {

	private static final Logger log = LoggerFactory.getLogger(NetGraphAdapter.class);

	private ApplicationModel appModel;

	static {
		try {
			String filename = NetGraphAdapter.class.getResource(
					"/graph/shapes.xml").getPath();
			Document doc = mxXmlUtils.parseXml(mxUtils.readFile(filename));
			Element shapes = doc.getDocumentElement();
			NodeList list = shapes.getElementsByTagName("shape");

			for (int i = 0; i < list.getLength(); i++)
			{
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
	}



	@Override
	public String convertValueToString(Object cell) {
		mxCell mxCell = (mxCell) cell;
		String value;
		if (mxCell.getValue() instanceof Line) {
			value = this.convertLine((Line) mxCell.getValue());
		} else if (mxCell.getValue() instanceof Node) {
			value = mxCell.getValue().toString();
		} else {
			value = "";
		}
		return value;
	}

	@Override
	public boolean isAutoSizeCell(Object cell) {
		return true;
	}

	@Override
	public boolean isCellSelectable(Object cell) {
		return cell != null ? (cell instanceof NodeCell) : false;
	}

	private String convertLine(Line line) {
		if (appModel == null)
			return line.toString();
		StringBuilder result = new StringBuilder();
		collectRoutes(line, result);
		if (result.length() != 0)
			result.append(';');
		LengthUnit lengthUnit = appModel.getProgramSettings().getLengthUnit();
		BigDecimal cValue = lengthUnit.convertFrom(new BigDecimal(line.getLength()), LengthUnit.MM);
		result.append(UnitUtil.convertToString("#0.###", cValue)).append(lengthUnit.getUnitsOfString());
		int topSpeed = line.getTopSpeed();
		if (topSpeed != Line.UNLIMITED_SPEED) {
			lengthUnit = appModel.getProgramSettings().getSpeedLengthUnit();
			BigDecimal sValue = lengthUnit.convertFrom(new BigDecimal(topSpeed), LengthUnit.KM);
			result.append(';').append(UnitUtil.convertToString("#0", sValue)).append(lengthUnit.getUnitsOfString())
					.append("/h");
		}
		return result.toString();
	}

	private void collectRoutes(Line line, StringBuilder builder) {
		TrainDiagram diagram = line.getTrainDiagram();
		boolean added = false;
		for (Route route : diagram.getRoutes()) {
			if (route.isNetPart()) {
				for (RouteSegment seg : route.getSegments()) {
					if (seg.asLine() != null && seg.asLine() == line) {
						if (added)
							builder.append(',');
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
			NodeShape shape = ((NodeCell) cell).getShape();
			result = new mxRectangle(0, 0, shape.getWidth(), shape.getHeight());
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
		cell.setStyle("shape=station;shadow=1;foldable=0;verticalLabelPosition=top");
		cell.setShape((NodeShape) mxStencilRegistry.getStencil("station"));
		cell.setGeometry(new mxGeometry());
		return cell;
	}

	@Override
	protected mxCell getEdgeCell(Line edge) {
		mxCell cell = new mxCell(edge);
		cell.setEdge(true);
		cell.setId(null);
		cell.setGeometry(new mxGeometry());
		cell.getGeometry().setRelative(true);
		cell.setStyle("endArrow=none;startArrow=none" + (edge.getTracks().size() == 1 ? "" : ";strokeWidth=2"));
		return cell;
	}
}
