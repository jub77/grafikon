package net.parostroj.timetable.model;

import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.OK;
import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.STOP_INCLUDE;

import net.parostroj.timetable.model.freight.FreightConnection;

/**
 * Filter.
 *
 * @author jub
 */
@FunctionalInterface
public interface FreightConnectionFilter {

    public enum FilterResult {
        OK, STOP_INCLUDE, STOP_EXCLUDE, IGNORE;

        public boolean isStop() {
            return this == STOP_EXCLUDE || this == STOP_INCLUDE;
        }

        public boolean isIncluded() {
            return this == STOP_INCLUDE || this == OK;
        }
    }

    public static class FilterContext {

        private final TimeInterval startInterval;

        public FilterContext(TimeInterval startInterval) {
            this.startInterval = startInterval;
        }

        public TimeInterval getStartInterval() {
            return startInterval;
        }
    }

    FilterResult accepted(FilterContext context, FreightConnection dst, int level);

    static FilterResult empty(FilterContext context, FreightConnection dst, int level) {
        return OK;
    }

    static FilterResult regionTransferStop(FilterContext context, FreightConnection dst, int level) {
        return dst.getTo().isRegions() ? STOP_INCLUDE : OK;
    }
}
