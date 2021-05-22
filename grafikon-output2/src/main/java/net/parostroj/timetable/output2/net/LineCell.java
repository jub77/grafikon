package net.parostroj.timetable.output2.net;

import net.parostroj.timetable.model.Line;

import com.mxgraph.model.mxCell;

/**
 * Line cell.
 *
 * @author jub
 */
public class LineCell extends mxCell {

    private static final long serialVersionUID = 1L;

    public LineCell(Object object) {
        super(object);
    }

    @Override
    public String getStyle() {
        Line line = (Line) value;
        StringBuilder style = new StringBuilder(super.getStyle());
        StyleHelper.multiTrack(style, line);
        StyleHelper.colorOfLine(style, line);
        return style.toString();
    }
}
