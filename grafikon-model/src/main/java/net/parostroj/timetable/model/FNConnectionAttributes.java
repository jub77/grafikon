package net.parostroj.timetable.model;

/**
 * Attributes for freight net connection.
 *
 * @author jub
 */
public interface FNConnectionAttributes {
    public static final String ATTR_LAST_NODES = "last.nodes";
    public static final String ATTR_LAST_NODES_EXCLUDE = "last.nodes.exclude";
    public static final String ATTR_FROM_NODES = "from.nodes";
    public static final String ATTR_TO_NODES = "to.nodes";
    public static final String ATTR_TRANSITION_LIMIT = "transition.limit";
}
