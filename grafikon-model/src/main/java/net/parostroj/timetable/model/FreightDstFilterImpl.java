package net.parostroj.timetable.model;

import java.util.List;

/**
 * @author jub
 */
public class FreightDstFilterImpl extends FreightDstFilter {

    private final FreightDstFilter parent;
    private final List<Node> lastNodes;
    private boolean stop;
    private final Integer transitionLimit;

    protected FreightDstFilterImpl(FreightDstFilter parent, List<Node> lastNodes, Integer transitionLimit) {
        this.parent = parent;
        this.lastNodes = lastNodes;
        this.transitionLimit = transitionLimit;
    }

    @Override
    public boolean accepted(FreightDst dst, int level) {
        if (transitionLimit != null && transitionLimit < level) {
            return false;
        }
        if (stop || (parent != null && !parent.accepted(dst, level + 1))) {
            return false;
        }
        if (lastNodes != null && dst.isNode() && lastNodes.contains(dst.getNode())) {
            stop = true;
        }
        return true;
    }

}
