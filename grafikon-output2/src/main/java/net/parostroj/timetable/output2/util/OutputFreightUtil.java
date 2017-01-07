package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

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

    public Collection<String> regionsToString(Collection<Region> regions, Collator collator) {
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
}
