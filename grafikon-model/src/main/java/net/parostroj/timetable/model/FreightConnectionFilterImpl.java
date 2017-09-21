package net.parostroj.timetable.model;

import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.IGNORE;
import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.OK;
import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.STOP_EXCLUDE;
import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.STOP_INCLUDE;

import java.util.List;

import net.parostroj.timetable.model.freight.FreightConnection;

/**
 * @author jub
 */
class FreightConnectionFilterImpl implements FreightConnectionFilter {

    private final FreightConnectionFilter parent;
    private List<Node> stopNodes;
    private List<Node> stopNodesExclude;
    private List<Node> fromNodes;
    private List<Node> toNodes;
    private Integer transitionLimit;

    protected FreightConnectionFilterImpl(FreightConnectionFilter parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null");
        }
        this.parent = parent;
    }

    public void setStopNodes(List<Node> stopNodes) {
        this.stopNodes = stopNodes;
    }

    public void setStopNodesExclude(List<Node> stopNodesExclude) {
        this.stopNodesExclude = stopNodesExclude;
    }

    public void setToNodes(List<Node> toNodes) {
        this.toNodes = toNodes;
    }

    public void setFromNodes(List<Node> fromNodes) {
        this.fromNodes = fromNodes;
    }

    public void setTransitionLimit(Integer transitionLimit) {
        this.transitionLimit = transitionLimit;
    }

    @Override
    public FilterResult accepted(FilterContext context, FreightConnection dst, int level) {
        if (transitionLimit != null && transitionLimit < level) {
            return STOP_EXCLUDE;
        }
        FilterResult parentResult = parent.accepted(context, dst, level + 1);
        FilterResult currentResult = getCurrentResult(context, dst);
        return currentResult.combine(parentResult);
    }

    private FilterResult getCurrentResult(FilterContext context, FreightConnection dst) {
        if (isInNodeList(dst, stopNodesExclude) || isNotInNodeList(context.getStartInterval(), fromNodes)) {
            return STOP_EXCLUDE;
        }
        if (isInNodeList(dst, stopNodes)) {
            return STOP_INCLUDE;
        }
        if (isNotInNodeList(dst, toNodes)) {
            return IGNORE;
        }
        return OK;
    }

    private boolean isInNodeList(FreightConnection dst, List<Node> nodes) {
        return nodes != null && nodes.contains(dst.getTo().getNode());
    }

    private boolean isNotInNodeList(FreightConnection dst, List<Node> nodes) {
        return nodes != null && !nodes.contains(dst.getTo().getNode());
    }

    private boolean isNotInNodeList(TimeInterval interval, List<Node> nodes) {
        return nodes != null && interval.isNodeOwner() && !nodes.contains(interval.getOwnerAsNode());
    }
}
