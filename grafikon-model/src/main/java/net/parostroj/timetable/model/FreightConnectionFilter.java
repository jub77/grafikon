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
