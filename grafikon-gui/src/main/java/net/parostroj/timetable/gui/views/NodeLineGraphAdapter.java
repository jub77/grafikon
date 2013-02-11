package net.parostroj.timetable.gui.views;

import java.math.BigDecimal;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;

import org.jgrapht.ListenableGraph;

import com.mxgraph.model.mxCell;

/**
 * Specific adapter for graph with nodes and lines.
 *
 * @author cz2b10k5
 */
public class NodeLineGraphAdapter extends JGraphXAdapter<Node, Line> {

	private ApplicationModel appModel;

	public NodeLineGraphAdapter(ListenableGraph<Node, Line> graphT, ApplicationModel model) {
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
		} else {
			value = mxCell.getValue().toString();
		}
		return value;
	}

	@Override
	public boolean isAutoSizeCell(Object cell) {
		return true;
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
}
