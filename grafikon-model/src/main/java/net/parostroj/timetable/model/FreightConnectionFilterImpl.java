package net.parostroj.timetable.model;

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
            return FilterResult.STOP_EXCLUDE;
        }
        FilterResult parentResult = parent.accepted(context, dst, level + 1);
        if (parentResult != FilterResult.OK) {
            return parentResult;
        }
        if (isInNodeList(dst, stopNodesExclude) || isNotInNodeList(context.getStartInterval(), fromNodes)) {
            return FilterResult.STOP_EXCLUDE;
        }
        if (isInNodeList(dst, stopNodes)) {
            return FilterResult.STOP_INCLUDE;
        }
        if (isNotInNodeList(dst, toNodes)) {
            return FilterResult.IGNORE;
        }
        return FilterResult.OK;
    }

    private boolean isInNodeList(FreightConnection dst, List<Node> nodes) {
        return nodes != null && nodes.contains(dst.getTo());
    }

    private boolean isNotInNodeList(FreightConnection dst, List<Node> nodes) {
        return nodes != null && !nodes.contains(dst.getTo());
    }

    private boolean isNotInNodeList(TimeInterval interval, List<Node> nodes) {
        return nodes != null && interval.isNodeOwner() && !nodes.contains(interval.getOwnerAsNode());
    }
}
