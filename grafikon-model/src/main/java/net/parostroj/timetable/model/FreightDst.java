package net.parostroj.timetable.model;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.actions.TextList;

/**
 * Destination for cargo.
 *
 * @author jub
 */
public class FreightDst {

    private final Node node;
    private final TimeInterval timeInterval;
    private final List<TimeInterval> path;

    public FreightDst(Node node, TimeInterval timeInterval) {
        this(node, timeInterval, null);
    }

    public FreightDst(Node node, TimeInterval timeInterval, List<TimeInterval> path) {
        this.node = node;
        this.timeInterval = timeInterval;
        this.path = path;
    }

    public Node getNode() {
        return node;
    }

    public List<Region> getRegions() {
        return node.getCenterRegions();
    }

    public Train getTrain() {
        return timeInterval.getTrain();
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public List<TimeInterval> getPath() {
        return path;
    }

    public boolean isCenter() {
        return node.isCenterOfRegions();
    }

    @Override
    public String toString() {
        return this.toString(Locale.getDefault());
    }

    public String toString(Locale locale) {
        return this.toString(locale, true);
    }

    public String toString(Locale locale, boolean abbreviation) {
        StringBuilder freightStr = new StringBuilder();
        StringBuilder colorsStr = null;
        List<?> cs = (List<?>) node.getAttributes().get(Node.ATTR_FREIGHT_COLORS);
        if (cs != null && !cs.isEmpty()) {
            colorsStr = new StringBuilder();
            TextList o = new TextList(colorsStr, "[", "]", ",");
            o.addItems(Iterables.filter(cs, FreightColor.class), color -> color.getName(locale));
            o.finish();
        }
        if (node.getType() != NodeType.STATION_HIDDEN || colorsStr == null) {
            freightStr.append(abbreviation ? node.getAbbr() : node.getName());
        }
        if (colorsStr != null) {
            freightStr.append(colorsStr.toString());
        }
        if (node.isCenterOfRegions()) {
            TextList o = new TextList(freightStr, "(", ")", ",");
            o.addItems(node.getCenterRegions());
            o.finish();
        }
        return freightStr.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
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
        return true;
    }
}
