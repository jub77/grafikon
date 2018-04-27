package net.parostroj.timetable.gui.views.graph;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;

/**
 * Utility class for modifying style for nodes and lines.
 *
 * @author jub
 */
public class StyleHelper {

    private static final String CONTROLLED_LINE_COLOR = "turquoise";
    private static final String MULTI_TRACK_WIDTH = "2";

    private StyleHelper() {}

    public static void multiTrack(StringBuilder style, Line line) {
        if (line.getTracks().size() > 1) {
            style.append(";strokeWidth=").append(MULTI_TRACK_WIDTH);
        }
    }

    public static void colorOfLine(StringBuilder style, Line line) {
        if (line.getAttributeAsBool(Line.ATTR_CONTROLLED)) {
            style.append(";strokeColor=").append(CONTROLLED_LINE_COLOR);
        }
    }

    public static void nodeShape(StringBuilder style, NodeShape nodeShape) {
        style.append(";shape=").append(nodeShape.getName());
    }

    public static void colorOfNode(StringBuilder style, Node node) {
        if (node.getAttributeAsBool(Node.ATTR_CONTROL_STATION)) {
            style.append(";strokeColor=").append(CONTROLLED_LINE_COLOR);
        }
    }
}
