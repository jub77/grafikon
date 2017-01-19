package net.parostroj.timetable.model;

import net.parostroj.timetable.model.FreightConnectionFilter.FilterResult;

/**
 * Filter factory.
 *
 * @author jub
 */
public class FreightConnectionFilterFactory {

    public static FreightConnectionFilter createEmptyFilter() {
        return (context, dst, limit) -> FilterResult.OK;
    }

    public static FreightConnectionFilter createFilter(FreightConnectionFilter currentFilter, FNConnection connection, boolean ignoreFrom) {
        FreightConnectionFilterImpl filter = new FreightConnectionFilterImpl(currentFilter);
        filter.setTransitionLimit(connection.get(FNConnection.ATTR_TRANSITION_LIMIT, Integer.class));
        filter.setStopNodes(connection.getAsList(FNConnection.ATTR_LAST_NODES, Node.class));
        filter.setStopNodesExclude(connection.getAsList(FNConnection.ATTR_LAST_NODES_EXCLUDE, Node.class));
        if (!ignoreFrom) {
            filter.setFromNodes(connection.getAsList(FNConnection.ATTR_FROM_NODES, Node.class));
        }
        filter.setToNodes(connection.getAsList(FNConnection.ATTR_TO_NODES, Node.class));
        return filter;
    }

}
