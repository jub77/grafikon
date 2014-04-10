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
    private final Train train;
    
    public FreightDst(Region region, Train train) {
        this.region = region;
        this.node = null;
        this.train = train;
    }

    public FreightDst(Node node, Train train) {
        this.region = null;
        this.node = node;
        this.train = train;
    }

    public Node getNode() {
        return node;
    }

    public Region getRegion() {
        return region != null ? region : node.getAttributes().get(Node.ATTR_REGION, Region.class);
    }

    public Train getTrain() {
        return train;
    }

    public boolean isNode() {
        return this.node != null;
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
