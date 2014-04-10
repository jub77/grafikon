package net.parostroj.timetable.actions;

import java.util.*;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.filters.FilteredIterable;
import net.parostroj.timetable.model.*;

/**
 * Tests for freight manipulations.
 *
 * @author jub
 */
public class FreightHelper {

    public static boolean isFreightFrom(TimeInterval interval) {
        return isFreightCommon(interval) && (interval.isFirst() || interval.getLength() > 0);
    }

    public static boolean isFreightTo(TimeInterval interval) {
        return isFreightCommon(interval) && (interval.isLast() || interval.getLength() > 0);
    }

    public static boolean isFreight(TimeInterval interval) {
        return isFreightCommon(interval) && (interval.isFirst() || interval.isLast() || interval.getLength() > 0);
    }

    private static boolean isFreightCommon(TimeInterval interval) {
        return interval.isNodeOwner() && isManaged(interval.getTrain())
                && !interval.getAttributes().getBool(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightFrom(Iterable<TimeInterval> i) {
        return new FilteredIterable<TimeInterval>(i, new Filter<TimeInterval>() {
            @Override
            public boolean is(TimeInterval instance) {
                return isFreightFrom(instance);
            }
        });
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightTo(Iterable<TimeInterval> i) {
        return new FilteredIterable<TimeInterval>(i, new Filter<TimeInterval>() {
            @Override
            public boolean is(TimeInterval instance) {
                return isFreightTo(instance);
            }
        });
    }

    public static Iterable<TimeInterval> getNodeIntervalsWithFreight(Iterable<TimeInterval> i, final TimeInterval from) {
        return new FilteredIterable<TimeInterval>(i, new Filter<TimeInterval>() {

            boolean after = false;

            @Override
            public boolean is(TimeInterval instance) {
                if (after) {
                    return FreightHelper.isFreightTo(instance);
                } else {
                    after = instance == from;
                    return false;
                }
            }
        });
    }

    public static List<FreightDst> convertFreightDst(Train train, Region region, List<FreightDst> list) {
        List<FreightDst> result = new LinkedList<FreightDst>();
        Set<Region> used = new HashSet<Region>();
        Set<FreightDst> visited = new HashSet<FreightDst>();
        for (FreightDst dst : list) {
            if (!dst.isNode()) {
                throw new IllegalArgumentException("Only node destinations allowed.");
            }
            if (visited.contains(dst)) {
                continue;
            }
            Region nRegion = dst.getRegion();
            if (nRegion != null && nRegion != region && train != dst.getTrain() &&
                    dst.getNode().getType() != NodeType.STATION_HIDDEN) {
                if (!used.contains(nRegion)) {
                    result.add(new FreightDst(nRegion, null));
                    used.add(nRegion);
                }
            } else {
                visited.add(dst);
                result.add(dst);
            }
        }
        return result;
    }

    public static String freightDstListToString(Collection<FreightDst> list) {
        StringBuilder builder = new StringBuilder();
        TextList output = new TextList(builder, ",");
        for (FreightDst dst : list) {
            output.add(dst.toString());
        }
        output.finish();
        return builder.toString();
    }

    public static boolean isManaged(Train train) {
        return train.getAttributes().getBool(Train.ATTR_MANAGED_FREIGHT);
    }
}
