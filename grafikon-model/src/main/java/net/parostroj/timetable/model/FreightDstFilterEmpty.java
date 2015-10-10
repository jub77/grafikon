package net.parostroj.timetable.model;

/**
 * @author jub
 */
public class FreightDstFilterEmpty implements FreightDstFilter {

    protected FreightDstFilterEmpty() {
    }

    @Override
    public FilterResult accepted(FilterContext context, FreightDst dst, int level) {
        return FilterResult.OK;
    }
}
