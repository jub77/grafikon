package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Utilities for output.
 *
 * @author jub
 */
public class OutputUtil {

    private OutputFreightUtil freight;

    public List<TimeInterval> sortIntervals(List<TimeInterval> intervals) {
        return intervals.stream()
                .sorted((o1, o2) -> o1.getInterval().getNormalizedStart() - o2.getInterval().getNormalizedStart())
                .collect(Collectors.toList());
    }

    public Collator getCollator(Locale locale) {
        return Collator.getInstance(locale);
    }

    public Comparator<String> getStringComparator(Locale locale) {
        return Collator.getInstance(locale)::compare;
    }

    public Locale getLocaleForNode(Node node, Locale defaultLocale) {
        // company has the highest priority
        Locale locale = node.getCompany() != null ? node.getCompany().getLocale() : null;
        // otherwise get locale from regions
        if (locale == null) {
            locale = node.getRegions().stream()
                    .map(region -> region.getLocale())
                    .filter(loc -> loc != null)
                    .findAny()
                    .orElse(null);
        }
        return locale == null ? defaultLocale : locale;
    }

    public OutputFreightUtil getFreight() {
        if (freight == null) {
            freight = new OutputFreightUtil();
        }
        return freight;
    }
}
