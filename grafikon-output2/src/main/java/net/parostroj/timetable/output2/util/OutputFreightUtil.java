package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.freight.FreightAnalyser;
import net.parostroj.timetable.model.freight.FreightConnection;
import net.parostroj.timetable.model.freight.FreightConnectionPath;
import net.parostroj.timetable.model.freight.FreightConnectionStrategy;
import net.parostroj.timetable.model.freight.FreightDestination;
import net.parostroj.timetable.model.freight.Transport;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.Pair;

/**
 * Utilities for output specific to dealing with freight.
 *
 * @author jub
 */
public class OutputFreightUtil {

    public FreightAnalyser createAnalyser(TrainDiagram diagram) {
        return new FreightAnalyser(FreightConnectionStrategy.createCached(diagram.getFreightNet().getConnectionStrategy()));
    }

    public List<String> regionsToString(Collection<Region> regions, Locale locale) {
        return this.regionsToStringImpl(regions, locale).collect(Collectors.toList());
    }

    private Stream<String> regionsToStringImpl(Collection<Region> regions, Locale locale) {
        Collator collator = Collator.getInstance(locale);
        return regions.stream()
                .map(Region::getName)
                .sorted(collator::compare);
    }

    public List<String> intervalsToString(TrainDiagram diagram, Collection<TimeInterval> intervals, Locale locale) {
        return this.intervalsToStringImpl(diagram, intervals, locale).collect(Collectors.toList());
    }

    private Stream<String> intervalsToStringImpl(TrainDiagram diagram, Collection<TimeInterval> intervals, Locale locale) {
        return intervals.stream()
                .sorted(Comparator.comparingInt(TimeInterval::getEnd))
                .map(i -> intervalToString(diagram, i, locale));
    }

    public String intervalToString(TrainDiagram diagram, TimeInterval interval, Locale locale) {
        String trainName = interval.getTrain().getName().translate(locale);
        String time = diagram.getTimeConverter().convertIntToText(interval.getEnd());
        return String.format("%s (%s)", trainName, time);
    }

    public List<String> intervalFreightTrainUnitToString(TrainDiagram diagram, TimeInterval interval) {
        Collection<TrainsCycleItem> items = interval.getTrain()
                .getCycleItemsForInterval(diagram.getTrainUnitCycleType(), interval);
        Stream<TrainsCycle> cycles = items.stream().map(TrainsCycleItem::getCycle)
                .filter(cycle -> cycle.getAttributeAsBool(TrainsCycle.ATTR_FREIGHT));
        return cycles.map(cycle -> {
            String desc = cycle.getDescription();
            if (ObjectsUtil.isEmpty(desc)) {
                return cycle.getName();
            } else {
                return String.format("%s (%s)", cycle.getName(), desc);
            }
        }).collect(Collectors.toList());
    }

    public List<String> transportToString(TrainDiagram diagram, Transport transport, Locale locale) {
        return Stream.concat(
                    this.regionsToStringImpl(transport.getRegions(), locale),
                    this.intervalsToStringImpl(diagram, transport.getTrains(), locale))
                .collect(Collectors.toList());
    }

    public String freightNodeToString(FreightDestination dest, Locale locale, boolean abbreviation) {
        if (!dest.isNodeDestination()) {
            throw new IllegalArgumentException("Destination is not to node: " + dest);
        }
        Node node = dest.getNode();
        return abbreviation ? node.getAbbr() : node.getName();
    }

    public List<String> freightColorsToString(FreightDestination dest, Locale locale) {
        if (!dest.isFreightColorsDestination()) {
            throw new IllegalArgumentException("Destination is not to freight colors: " + dest);
        }
        return sortFreightColors(dest.getFreightColors()).stream()
                .map(color -> color.getName(locale))
                .collect(Collectors.toList());
    }

    public List<String> freightRegionsToString(FreightDestination dest, Locale locale) {
        if (!dest.isRegionsDestination()) {
            throw new IllegalArgumentException("Destination is not to regions: " + dest);
        }
        Set<Region> regions = dest.getRegions();
        return regionsToString(regions, locale);
    }

    /**
     * Reorders list of freight connections based on direction. The output
     * is ordered in that way that always the last part of train is left in
     * the station.
     *
     * @param list list of freight connections
     * @return list reordered by direction
     */
    public List<FreightConnection> reorderFreightListByDirection(
            Collection<? extends FreightConnection> list) {
        List<Pair<Boolean, FreightConnection>> rr = list.stream()
                .map(d -> new Pair<>(d instanceof FreightConnectionPath && ((FreightConnectionPath) d).getPath()
                        .isDirectionReversed(), (FreightConnection) d))
                .collect(Collectors.toList());
        rr = Lists.reverse(rr);
        LinkedList<FreightConnection> result = new LinkedList<>();
        Iterator<Pair<Boolean, FreightConnection>> i = rr.iterator();
        Pair<Boolean, FreightConnection> current = i.hasNext() ? i.next() : null;
        if (current != null) {
            result.add(current.second);
        }
        while (current != null) {
            Pair<Boolean, FreightConnection> previous = current;
            current = i.hasNext() ? i.next() : null;
            if (current != null) {
                if (current.first == previous.first) {
                    if (!current.first) result.addFirst(current.second); else result.addLast(current.second);
                } else {
                    if (current.first) result.addFirst(current.second); else result.addLast(current.second);
                }
            }
        }
        return result;
    }

    /**
     * Method returns list of strings with destinations. It automatically
     * reorders the list by direction (the method
     * {@link #reorderFreightListByDirection(Collection)} shouldn't be called
     * before calling this method.
     *
     * @param list list of freight connections
     * @param locale locale of the output
     * @return list of destination (uses abbreviation for stations)
     */
    public List<String> freightListToString(
            Collection<? extends FreightConnection> list, Locale locale) {
        return this.reorderFreightListByDirection(list).stream()
                .map(d -> freightToString(d.getTo(), locale))
                .collect(Collectors.toList());
    }

    public List<String> freightListToString(
            Collection<? extends FreightConnection> list,
            Locale locale, BiFunction<FreightConnection, String, String> conversion) {
        return this.reorderFreightListByDirection(list).stream()
                .map(d -> conversion.apply(d, freightToString(d.getTo(), locale)))
                .collect(Collectors.toList());
    }

    public String freightToString(FreightDestination dest, Locale locale) {
        StringBuilder result = new StringBuilder();
        if (dest.isNodeDestination() && dest.isVisible()) {
            result.append(freightNodeToString(dest, locale, true));
        }
        if (dest.isFreightColorsDestination()) {
            result.append('[').append(String.join(",", freightColorsToString(dest, locale)))
                    .append(']');
        }
        if (dest.isRegionsDestination()) {
            boolean empty = result.length() == 0;
            if (!empty) result.append('(');
            result.append(String.join(",", freightRegionsToString(dest, locale)));
            if (!empty) result.append(')');
        }
        return result.toString();
    }

    public static List<FreightColor> sortFreightColors(Collection<FreightColor> colors) {
        if (colors.isEmpty()) return Collections.emptyList();
        return colors.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<Region> sortRegions(Collection<Region> regions, Locale locale) {
        if (regions.isEmpty()) return Collections.emptyList();
        final Collator collator = Collator.getInstance(locale);
        return regions.stream()
                .sorted((a, b) -> collator.compare(a.getName(), b.getName()))
                .collect(Collectors.toList());
    }
}
