package net.parostroj.timetable.output2.util;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
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
    private final Escaper escaper = HtmlEscapers.htmlEscaper();

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
        return Comparator.comparingInt(a -> a.getInterval().getNormalizedStart());
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

    public String escape(String string) {
        return escaper.escape(string);
    }

    public OutputFreightUtil getFreight() {
        if (freight == null) {
            freight = new OutputFreightUtil();
        }
        return freight;
    }
}
