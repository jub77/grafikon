package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.NodeType;

/**
 * NodeType wrapper delegate.
 *
 * @author jub
 */
public class NodeTypeWrapperDelegate extends BasicWrapperDelegate<NodeType> {

    @Override
    public String toString(NodeType element) {
        return ResourceLoader.getString("node." + element.getKey() + ".text");
    }
}
