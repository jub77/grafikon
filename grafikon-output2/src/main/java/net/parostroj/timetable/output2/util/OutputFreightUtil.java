package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.actions.TextList;
import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.FreightDestination;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.freight.FreightAnalyser;

/**
 * Utilities for output specific to dealing with freight.
 *
 * @author jub
 */
public class OutputFreightUtil {

    public FreightAnalyser createAnalyser(TrainDiagram diagram) {
        return new FreightAnalyser(diagram);
    }

    public List<String> regionsToString(Collection<Region> regions, Collator collator) {
        return regions.stream()
                .map(r -> r.getName())
                .sorted((a, b) -> collator.compare(a, b))
                .collect(Collectors.toList());
    }

    public Collection<String> intervalsToString(TrainDiagram diagram, Collection<TimeInterval> intervals, Locale locale) {
        return intervals.stream()
                .sorted((a, b) -> Integer.compare(a.getEnd(), b.getEnd()))
                .map(i -> {
                    String trainName = i.getTrain().getName().translate(locale);
                    String time = diagram.getTimeConverter().convertIntToText(i.getEnd());
                    return String.format("%s (%s)", trainName, time);
                })
                .collect(Collectors.toList());
    }

    // TODO rewrite -- node is not always the only destination
    public String freightNodeToString(FreightDestination dest, Locale locale, boolean abbreviation) {
        Node node = dest.getTo();
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
        return freightStr.toString();
    }

    // TODO rewrite -- sort regions ...
    public String freightRegionsToString(FreightDestination dest, Locale locale) {
        if (!dest.isCenterOfRegions()) {
            throw new IllegalArgumentException("Destination is not center of regions");
        }
        Set<Region> regions = dest.getTargetRegionsFrom();
        return regionsToString(regions, Collator.getInstance(locale))
                .stream()
                .collect(Collectors.joining(","));
    }

    public String freightListToString(Collection<? extends FreightDestination> list) {
        return this.freightListToString(list, Locale.getDefault());
    }

    public String freightListToString(Collection<? extends FreightDestination> list, Locale locale) {
        return list.stream().map(d -> {
            String destString = freightNodeToString(d, locale, true);
            return d.isCenterOfRegions() ? String.format("%s(%s)", destString, freightRegionsToString(d, locale)): destString;
        }).collect(Collectors.joining(","));
    }
}
