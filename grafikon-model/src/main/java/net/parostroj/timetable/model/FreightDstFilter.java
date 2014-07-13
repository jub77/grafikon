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

    public FreightDstFilter(FreightDstFilter parent, FNConnection connection) {
        this.parent = parent;
        this.lastNodes = connection != null ? connection.get(FNConnection.ATTR_LAST_NODES, List.class) : null;
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
