package net.parostroj.timetable.gui.helpers;

import net.parostroj.timetable.model.Node;

/**
 * Node wrapper.
 *
 * @author jub
 */
public class NodeWrapper extends Wrapper<Node> {

    public NodeWrapper(Node node) {
        super(node);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return toString(getElement());
    }

    public static String toString(Node node) {
        return node.getName();
    }
}
