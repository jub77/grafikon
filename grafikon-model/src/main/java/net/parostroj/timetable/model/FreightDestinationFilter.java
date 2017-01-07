package net.parostroj.timetable.model;

/**
 * Filter.
 *
 * @author jub
 */
public interface FreightDestinationFilter {

    public enum FilterResult {
        OK, STOP_INCLUDE, STOP_EXCLUDE, IGNORE
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

    FilterResult accepted(FilterContext context, FreightDestination dst, int level);
}
