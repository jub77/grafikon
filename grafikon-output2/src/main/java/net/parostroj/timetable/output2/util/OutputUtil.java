package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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

    private OutputFreightUtil freight;

    public List<TimeInterval> sortIntervals(Collection<TimeInterval> intervals) {
        return intervals.stream()
                .sorted(getNormalizedStartComparator())
                .collect(Collectors.toList());
    }

    public Collator getCollator(Locale locale) {
        return Collator.getInstance(locale);
    }

    public Comparator<String> getStringComparator(Locale locale) {
        return Collator.getInstance(locale)::compare;
    }

    public Comparator<TimeInterval> getNormalizedStartComparator() {
        return (a, b) -> Integer.compare(a.getInterval().getNormalizedStart(), b.getInterval().getNormalizedStart());
    }

    public Locale getLocaleForNode(Node node, Locale defaultLocale) {
        // company has the highest priority
        Locale locale = node.getCompany() != null ? node.getCompany().getLocale() : null;
        // otherwise get locale from regions
        if (locale == null) {
            locale = node.getRegions().stream()
                    .map(Region::getLocale)
                    .filter(Objects::nonNull)
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
