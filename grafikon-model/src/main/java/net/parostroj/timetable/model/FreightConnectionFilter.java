package net.parostroj.timetable.model;

import net.parostroj.timetable.model.freight.FreightConnection;

/**
 * Filter.
 *
 * @author jub
 */
public interface FreightConnectionFilter {

    public enum FilterResult {
        OK, STOP_INCLUDE, STOP_EXCLUDE, IGNORE;

        public boolean isStop() {
            return this == STOP_EXCLUDE || this == STOP_INCLUDE;
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
}
