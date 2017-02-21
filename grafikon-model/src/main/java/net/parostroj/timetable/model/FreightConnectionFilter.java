package net.parostroj.timetable.model;

import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.OK;

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
}
