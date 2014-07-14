package net.parostroj.timetable.model;

import java.util.List;

/**
 * @author jub
 */
public class FreightDstFilterImpl extends FreightDstFilter {

    private final FreightDstFilter parent;
    private final List<?> lastNodes;
    private boolean stop;

    protected FreightDstFilterImpl(FreightDstFilter parent, List<?> lastNodes) {
        this.parent = parent;
        this.lastNodes = lastNodes;
    }

    @Override
    public boolean accepted(FreightDst dst, int level) {
        if (stop || (parent != null && !parent.accepted(dst, level + 1))) {
            return false;
        }
        if (lastNodes != null && dst.isNode() && lastNodes.contains(dst.getNode())) {
            stop = true;
        }
        return true;
    }

}
