package net.parostroj.timetable.model;

import net.parostroj.timetable.model.freight.FreightConnection;

/**
 * Filter.
 *
 * @author jub
 */
@FunctionalInterface
public interface FreightConnectionFilter {

    public enum FilterResult {
        OK("ok"), STOP_INCLUDE("stop_include"), STOP_EXCLUDE("stop_exclude"), IGNORE("ignore");

        private String key;

        private FilterResult(String key) {
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
            FilterResult result = OK;
            switch (previous) {
                case OK:
                    result = this;
                    break;
                case IGNORE:
                    result = this.isStop() ? STOP_EXCLUDE : IGNORE;
                    break;
                case STOP_EXCLUDE:
                    result = STOP_EXCLUDE;
                    break;
                case STOP_INCLUDE:
                    result = !this.isIncluded() ? STOP_EXCLUDE : STOP_INCLUDE;
                    break;
            }
            return result;
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

    /**
     * @return always returns OK as result of a filter
     */
    static FilterResult empty(FilterContext context, FreightConnection dst, int level) {
        return FilterResult.OK;
    }
}
