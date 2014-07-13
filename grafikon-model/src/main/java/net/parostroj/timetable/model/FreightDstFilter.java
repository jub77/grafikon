package net.parostroj.timetable.model;

import java.util.List;

/**
 * Filtering freight searching. It holds state.
 *
 * @author jub
 */
public class FreightDstFilter {

    private final FreightDstFilter parent;
    private final List<?> lastNodes;
    private boolean stop;

    public static FreightDstFilter createFilter(FreightDstFilter current, FNConnection connection) {
        List<?> lastNodes = connection.get(FNConnection.ATTR_LAST_NODES, List.class);
        return lastNodes == null ? current : new FreightDstFilter(current, lastNodes);
    }

    protected FreightDstFilter(FreightDstFilter parent, List<?> lastNodes) {
        this.parent = parent;
        this.lastNodes = lastNodes;
    }

    public boolean accepted(FreightDst dst) {
        if (stop || (parent != null && !parent.accepted(dst))) {
            return false;
        }
        if (lastNodes != null && dst.isNode() && lastNodes.contains(dst.getNode())) {
            stop = true;
        }
        return true;
    }
}
