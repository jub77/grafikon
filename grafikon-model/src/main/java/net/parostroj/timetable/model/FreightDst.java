package net.parostroj.timetable.model;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.actions.FreightHelper;
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
    private final List<TimeInterval> path;

    public FreightDst(Region region, Train train) {
        this.region = region;
        this.node = null;
        this.train = train;
        this.path = null;
    }

    public FreightDst(Node node, Train train) {
        this(node, train, null);
    }

    public FreightDst(Node node, Train train, List<TimeInterval> path) {
        this.region = null;
        this.node = node;
        this.train = train;
        this.path = path;
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

    public List<TimeInterval> getPath() {
        return path;
    }

    public boolean isNode() {
        return this.node != null;
    }

    @Override
    public String toString() {
        return this.toString(Locale.getDefault());
    }

    public String toString(Locale locale) {
        StringBuilder colorsStr = null;
        if (node != null) {
            List<?> cs = (List<?>) node.getAttributes().get(Node.ATTR_FREIGHT_COLORS);
            if (cs != null && !cs.isEmpty()) {
                colorsStr = new StringBuilder();
                TextList o = new TextList(colorsStr, "[", "]", ",");
                o.addItems(Iterables.filter(cs, FreightColor.class), FreightHelper.colorToString(locale));
                o.finish();
            }
        }
        return node == null ? region.getName() : node.getAbbr() + (colorsStr != null ? colorsStr.toString() : "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FreightDst other = (FreightDst) obj;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        return true;
    }
}
