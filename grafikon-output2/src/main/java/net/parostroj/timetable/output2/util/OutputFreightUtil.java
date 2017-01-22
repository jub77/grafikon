package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.freight.FreightAnalyser;
import net.parostroj.timetable.model.freight.FreightConnection;
import net.parostroj.timetable.model.freight.Transport;

/**
 * Utilities for output specific to dealing with freight.
 *
 * @author jub
 */
public class OutputFreightUtil {

    public FreightAnalyser createAnalyser(TrainDiagram diagram) {
        return new FreightAnalyser(diagram);
    }

    public List<String> regionsToString(Collection<Region> regions, Locale locale) {
        Collator collator = Collator.getInstance(locale);
        return regions.stream()
                .map(r -> r.getName())
                .sorted((a, b) -> collator.compare(a, b))
                .collect(Collectors.toList());
    }

    public List<String> intervalsToString(TrainDiagram diagram, Collection<TimeInterval> intervals, Locale locale) {
        return intervals.stream()
                .sorted((a, b) -> Integer.compare(a.getEnd(), b.getEnd()))
                .map(i -> {
                    String trainName = i.getTrain().getName().translate(locale);
                    String time = diagram.getTimeConverter().convertIntToText(i.getEnd());
                    return String.format("%s (%s)", trainName, time);
                })
                .collect(Collectors.toList());
    }

    public List<String> transportToString(TrainDiagram diagram, Transport transport, Locale locale) {
        if (transport.isDirect()) {
            return this.intervalsToString(diagram, transport.getTrains(), locale);
        } else {
            return this.regionsToString(transport.getConnection().getTo().getRegions(), locale);
        }
    }

    // TODO rewrite -- node is not always the only destination
    public String freightNodeToString(FreightConnection dest, Locale locale, boolean abbreviation) {
        if (!dest.getTo().isNode()) {
            throw new IllegalArgumentException("Destination is not node: " + dest);
        }
        Node node = dest.getTo().getNode();
        return abbreviation ? node.getAbbr() : node.getName();
    }

    public String freightColorsToString(Collection<FreightColor> colors, Locale locale) {
        return sortFreightColors(colors).stream()
                .map(color -> color.getName(locale))
                .collect(Collectors.joining(","));
    }

    // TODO rewrite -- sort regions ...
    public String freightRegionToString(FreightConnection dest, Locale locale) {
        if (!dest.getTo().isRegions()) {
            throw new IllegalArgumentException("Destination is not center of regions: " + dest);
        }
        Set<Region> regions = dest.getTo().getRegions();
        return regionsToString(regions, locale).stream()
                .collect(Collectors.joining(","));
    }

    public List<String> freightListToString(Collection<? extends FreightConnection> list, Locale locale) {
        return list.stream().map(d -> {
            String destString = freightNodeToString(d, locale, true);
            return d.getTo().isRegions() ? String.format("%s(%s)", destString, freightRegionToString(d, locale)): destString;
        }).collect(Collectors.toList());
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
