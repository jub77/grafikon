package net.parostroj.timetable.model;

/**
 * @author jub
 */
public class FreightDstFilterEmpty extends FreightDstFilter {

    protected FreightDstFilterEmpty() {
    }

    @Override
    public boolean accepted(FreightDst dst, int level) {
        return true;
    }
}
