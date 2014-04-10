package net.parostroj.timetable.model;

import java.util.List;

import net.parostroj.timetable.actions.TextList;

/**
 * Destination for cargo.
 *
 * @author jub
 */
public class FreightDst {

    private final Node node;
    private final Region region;

    public FreightDst(Region region) {
        this.region = region;
        this.node = null;
    }

    public FreightDst(Node node) {
        this.region = null;
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        StringBuilder colorsStr = null;
        if (node != null) {
            List<?> cs = (List<?>) node.getAttributes().get(Node.ATTR_FREIGHT_COLORS);
            if (cs != null && !cs.isEmpty()) {
                colorsStr = new StringBuilder();
                TextList o = new TextList(colorsStr, "[", "]", ",");
                for (Object i : cs) {
                    o.add(((FreightColor) i).getName());
                }
                o.finish();
            }
        }
        return node == null ? region.getName() : node.getAbbr() + (colorsStr != null ? colorsStr.toString() : "");
    }
}
