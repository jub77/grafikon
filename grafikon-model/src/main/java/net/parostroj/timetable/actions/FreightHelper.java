package net.parostroj.timetable.actions;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ReferenceHolder;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Tests for freight manipulations.
 *
 * @author jub
 */
public class FreightHelper {

    public static boolean isFreightFrom(TimeInterval interval) {
        return isFreightCommon(interval) && (interval.isFirst() || interval.isInnerStop());
    }

    public static boolean isFreightTo(TimeInterval interval) {
        return isFreightCommon(interval) && (interval.isLast() || interval.isInnerStop());
    }

    public static boolean isFreight(TimeInterval interval) {
        return isFreightCommon(interval) && interval.isStop();
    }

    public static boolean isConnection(TimeInterval interval, FreightNet net) {
        return !net.getTrainsFrom(interval).isEmpty();
    }

    private static boolean isFreightCommon(TimeInterval interval) {
        return isManaged(interval.getTrain()) && !interval.getAttributes().getBool(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightFrom(Iterable<TimeInterval> i) {
        return Iterables.filter(i, interval -> isFreightFrom(interval));
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightTo(Iterable<TimeInterval> i) {
        return Iterables.filter(i, interval -> isFreightTo(interval));
    }

    public static Iterable<TimeInterval> getNodeIntervalsWithFreight(Iterable<TimeInterval> i, final TimeInterval from) {
        final ReferenceHolder<Boolean> after = new ReferenceHolder<Boolean>(false);
        return Iterables.filter(i, interval -> {
            if (after.get()) {
                return FreightHelper.isFreightTo(interval);
            } else {
                after.set(interval == from);
                return false;
            }
        });
    }

    public static Iterable<TimeInterval> getNodeIntervalsWithFreightOrConnection(Iterable<TimeInterval> i, final TimeInterval from, final FreightNet net) {
        final ReferenceHolder<Boolean> after = new ReferenceHolder<Boolean>(false);
        return Iterables.filter(i, interval -> {
            if (after.get()) {
                return FreightHelper.isFreightTo(interval) || FreightHelper.isConnection(interval, net);
            } else {
                after.set(interval == from);
                return false;
            }
        });
    }

    public static List<FreightDst> convertFreightDst(TimeInterval from, List<FreightDst> list) {
        Region region = from.getOwnerAsNode().getAttributes().get(Node.ATTR_REGION, Region.class);
        return convertFreightDst(from.getTrain(), region, list);
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
            boolean regionMatch = nRegion != null && nRegion != region;
            boolean otherTrain = train != dst.getTrain();
            if (regionMatch && otherTrain && dst.getNode().getType() != NodeType.STATION_HIDDEN) {
                addRegion(result, used, nRegion);
            } else {
                visited.add(dst);
                result.add(dst);
            }
            if (isStartRegion(dst.getNode()) && nRegion != null) {
                addRegion(result, used, nRegion);
            }
        }
        return result;
    }

    private static void addRegion(List<FreightDst> result, Set<Region> used, Region nRegion) {
        if (!used.contains(nRegion)) {
            result.add(new FreightDst(nRegion, null));
            used.add(nRegion);
        }
    }

    public static String freightDstListToString(Collection<FreightDst> list) {
        StringBuilder builder = new StringBuilder();
        TextList output = new TextList(builder, ",");
        output.addItems(list);
        output.finish();
        return builder.toString();
    }

    public static boolean isManaged(Train train) {
        return train.getAttributes().getBool(Train.ATTR_MANAGED_FREIGHT);
    }

    public static boolean isNoTransitiveRegionStart(TimeInterval interval) {
        return isNoTransitiveRegionStart(interval.getTrain());
    }

    public static boolean isNoTransitiveRegionStart(Train train) {
        return train.getAttributes().getBool(Train.ATTR_NO_TRANSITIVE_REGION_START);
    }

    public static boolean isStartRegion(Node node) {
        return node.getAttributes().getBool(Node.ATTR_REGION_START);
    }

    public static boolean isStartRegion(TimeInterval interval) {
        return isStartRegion(interval.getOwnerAsNode());
    }

    public static boolean isRegionTransferTrain(Train train) {
        return isFreightFrom(train.getFirstInterval()) && isFreightTo(train.getLastInterval())
                && isStartRegion(train.getFirstInterval()) && isStartRegion(train.getLastInterval());
    }

    public static Function<FreightColor, String> colorToString(final Locale loc) {
        return color -> color.getName(loc);
    }
}
