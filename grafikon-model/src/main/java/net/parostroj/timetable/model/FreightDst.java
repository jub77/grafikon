package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    public FreightDst(Node node) {
        this(node, null, null);
    }

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

    public Set<Region> getRegions() {
        return node.getCenterRegions();
    }

    public List<Region> getSortedRegions() {
        return node.getSortedCenterRegions();
    }

    public List<Region> getSortedRegions(Locale locale) {
        return node.getSortedCenterRegions(locale);
    }

    public List<Region> getSortedRegionsFrom(Locale locale, Node from) {
        return Node.sortRegions(locale, getTargetRegionsFrom(from));
    }

    public Set<Region> getTargetRegionsFrom(Node from) {
        Node to = this.node;
        if (!to.isCenterOfRegions()) throw new IllegalArgumentException("No center of region: " + to);
        Set<Region> toCenterRegions = to.getCenterRegions();
        Set<Region> fromRegions = from.getRegions();
        return getTargetRegionsFrom(toCenterRegions, fromRegions);
    }

    public static Set<Region> getTargetRegionsFrom(Set<Region> toCenterRegions, Set<Region> fromRegions) {
        Region toSuper = toCenterRegions.isEmpty() ? null : getSuperRegion(toCenterRegions);
        Region fromSuper = fromRegions.isEmpty() ? null : getSuperRegion(fromRegions);
        // all center regions has to have the same super region (if exists)
        if (!toCenterRegions.isEmpty() && !fromRegions.isEmpty()) {
            if (fromSuper == null && toSuper != null) {
                toCenterRegions = Collections.singleton(toSuper.getTopSuperRegion());
            } else if (toSuper != null && !fromSuper.containsInHierarchy(toSuper)) {
                Region dest = toSuper;
                while (dest.getSuperRegion() != null && !fromSuper.containsInHierarchy(dest.getSuperRegion())) {
                    dest = dest.getSuperRegion();
                }
                toCenterRegions = Collections.singleton(dest);
            }
        }
        return toCenterRegions;
    }

    // returns super region - the assumption is that regions share the same super region
    private static Region getSuperRegion(Collection<? extends Region> toCenterRegions) {
        return toCenterRegions.iterator().next().getSuperRegion();
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
        boolean noCenter = timeInterval != null && timeInterval.getAttributeAsBool(TimeInterval.ATTR_NO_REGION_CENTER_TRANSFER);
        return node.isCenterOfRegions() && !noCenter;
    }

    @Override
    public String toString() {
        return this.toString(Locale.getDefault());
    }

    public String toString(Locale locale) {
        return this.toString(locale, true);
    }

    public String toString(Locale locale, boolean abbreviation) {
        return this.toString(locale, null, abbreviation, true);
    }

    public String toString(Locale locale, Node from, boolean abbreviation, boolean center) {
        StringBuilder freightStr = new StringBuilder();
        StringBuilder colorsStr = null;
        Collection<FreightColor> cs = node.getSortedFreightColors();
        if (cs != null && !cs.isEmpty()) {
            colorsStr = new StringBuilder();
            new TextList(colorsStr, "[", "]", ",")
                    .addItems(Iterables.filter(cs, FreightColor.class), color -> color.getName(locale)).finish();
        }
        if (node.getType() != NodeType.STATION_HIDDEN || colorsStr == null) {
            freightStr.append(abbreviation ? node.getAbbr() : node.getName());
        }
        if (colorsStr != null) {
            freightStr.append(colorsStr.toString());
        }
        if (this.isCenter() && center) {
            List<Region> regions = from == null ? node.getSortedCenterRegions(locale)
                    : this.getSortedRegionsFrom(locale, from);
            new TextList(freightStr, "(", ")", ",").addItems(regions).finish();
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
