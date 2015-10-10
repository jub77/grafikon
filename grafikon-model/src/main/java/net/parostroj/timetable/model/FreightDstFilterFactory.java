package net.parostroj.timetable.model;

import java.util.List;

import net.parostroj.timetable.model.FreightDstFilter.FilterResult;

/**
 * Filter factory.
 *
 * @author jub
 */
public class FreightDstFilterFactory {

    public static FreightDstFilter createEmptyFilter() {
        return (context, dst, limit) -> FilterResult.OK;
    }

    public static FreightDstFilter createFilter(FreightDstFilter current, FNConnection connection) {
        List<Node> lastNodes = connection.getAsList(FNConnection.ATTR_LAST_NODES, Node.class);
        return new FreightDstFilterImpl(current, lastNodes, connection.get(FNConnection.ATTR_TRANSITION_LIMIT, Integer.class));
    }

}
