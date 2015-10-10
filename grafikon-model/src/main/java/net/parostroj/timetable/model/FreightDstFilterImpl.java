package net.parostroj.timetable.model;

import java.util.List;

/**
 * @author jub
 */
public class FreightDstFilterImpl implements FreightDstFilter {

    private final FreightDstFilter parent;
    private final List<Node> lastNodes;
    private final Integer transitionLimit;

    protected FreightDstFilterImpl(FreightDstFilter parent, List<Node> lastNodes, Integer transitionLimit) {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null");
        }
        this.parent = parent;
        this.lastNodes = lastNodes;
        this.transitionLimit = transitionLimit;
    }

    @Override
    public FilterResult accepted(FilterContext context, FreightDst dst, int level) {
        if (transitionLimit != null && transitionLimit < level) {
            return FilterResult.STOP_EXCLUDE;
        }
        FilterResult parentResult = parent.accepted(context, dst, level + 1);
        if (parentResult != FilterResult.OK) {
            return parentResult;
        }
        if (lastNodes != null && dst.isNode() && lastNodes.contains(dst.getNode())) {
            return FilterResult.STOP_INCLUDE;
        }
        return FilterResult.OK;
    }

}
