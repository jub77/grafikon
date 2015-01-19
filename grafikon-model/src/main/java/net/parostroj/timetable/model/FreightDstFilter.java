package net.parostroj.timetable.model;

import java.util.List;

/**
 * Filtering freight searching. It holds state.
 *
 * @author jub
 */
public abstract class FreightDstFilter {

    public static FreightDstFilter createFilter() {
        return new FreightDstFilterEmpty();
    }

    public static FreightDstFilter createFilter(FreightDstFilter current, FNConnection connection) {
        List<?> lastNodes = connection.get(FNConnection.ATTR_LAST_NODES, List.class);
        return new FreightDstFilterImpl(current, lastNodes, connection.get(FNConnection.ATTR_TRANSITION_LIMIT, Integer.class));
    }

    public abstract boolean accepted(FreightDst dst, int level);
}
