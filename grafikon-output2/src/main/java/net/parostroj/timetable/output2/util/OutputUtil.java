package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Utilities for output.
 *
 * @author jub
 */
public class OutputUtil {

    public List<TimeInterval> sortIntervals(List<TimeInterval> intervals) {
        return intervals.stream()
                .sorted((o1, o2) -> o1.getInterval().getNormalizedStart() - o2.getInterval().getNormalizedStart())
                .collect(Collectors.toList());
    }

    public Collator getCollator(Locale locale) {
        return Collator.getInstance(locale);
    }

    public Locale getLocaleForNode(Node node, Locale defaultLocale) {
        Locale locale = defaultLocale;
        for (Region region : node.getRegions()) {
            Locale regionLocale = region.getLocale();
            if (regionLocale != null) {
                locale = regionLocale;
            }
        }
        if (node.getCompany() != null && node.getCompany().getLocale() != null) {
            locale = node.getCompany().getLocale();
        }
        return locale;
    }
}
