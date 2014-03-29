package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.model.Line;

import com.mxgraph.model.mxCell;

/**
 * Line cell.
 *
 * @author jub
 */
public class LineCell extends mxCell {

    public LineCell(Object object) {
        super(object);
    }

    @Override
    public String getStyle() {
        String style = ((Line) value).getTracks().size() == 1 ? "" : "strokeWidth=2;";
        return style + super.getStyle();
    }
}
