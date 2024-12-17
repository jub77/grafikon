package net.parostroj.timetable.model;

import net.parostroj.timetable.model.freight.FreightConnection;

/**
 * Filter.
 *
 * @author jub
 */
@FunctionalInterface
public interface FreightConnectionFilter {

    enum FilterResult {
        OK("ok"), STOP_INCLUDE("stop_include"), STOP_EXCLUDE("stop_exclude"), IGNORE("ignore");

        private final String key;

        FilterResult(String key) {
            this.key = key;
        }

        public boolean isStop() {
            return this == STOP_EXCLUDE || this == STOP_INCLUDE;
        }

        public boolean isIncluded() {
            return this == STOP_INCLUDE || this == OK;
        }

        /**
         * @return converts from string key to FilterResult, in case of unknown key, it returns OK.
         */
        public static FilterResult fromString(String key) {
            if (key == null) {
                return OK;
            } else {
                for (FilterResult result : values()) {
                    if (result.key.equals(key)) {
                        return result;
                    }
                }
                return OK;
            }
        }

        public String getKey() {
            return key;
        }

        /**
         * Combines current filter result with previous one.
         *
         * @param previous previous filter result
         * @return combined result
         */
        public FilterResult combine(FilterResult previous) {
            return switch (previous) {
                case OK -> this;
                case IGNORE -> this.isStop() ? STOP_EXCLUDE : IGNORE;
                case STOP_EXCLUDE -> STOP_EXCLUDE;
                case STOP_INCLUDE -> !this.isIncluded() ? STOP_EXCLUDE : STOP_INCLUDE;
            };
        }
    }

    class FilterContext {

        private final TimeInterval startInterval;

        public FilterContext(TimeInterval startInterval) {
            this.startInterval = startInterval;
        }

        public TimeInterval getStartInterval() {
            return startInterval;
        }
    }

    FilterResult accepted(FilterContext context, FreightConnection dst, int level);

    /**
     * @return always returns OK as result of a filter
     */
    static FilterResult empty(FilterContext context, FreightConnection dst, int level) {
        return FilterResult.OK;
    }
}
