package net.parostroj.timetable.model;

/**
 * Filter.
 *
 * @author jub
 */
public interface FreightDstFilter {

    public enum FilterResult {
        OK, STOP_INCLUDE, STOP_EXCLUDE, IGNORE
    }

    FilterResult accepted(FreightDst dst, int level);
}