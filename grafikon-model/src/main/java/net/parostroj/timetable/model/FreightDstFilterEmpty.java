package net.parostroj.timetable.model;

/**
 * @author jub
 */
public class FreightDstFilterEmpty extends FreightDstFilter {

    protected FreightDstFilterEmpty() {
    }

    @Override
    public FilterResult accepted(FreightDst dst, int level) {
        return FilterResult.OK;
    }
}
