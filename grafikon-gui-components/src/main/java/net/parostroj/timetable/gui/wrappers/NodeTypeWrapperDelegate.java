package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.NodeType;

/**
 * NodeType wrapper delegate.
 *
 * @author jub
 */
public class NodeTypeWrapperDelegate extends BasicWrapperDelegate {

    @Override
    public String toString(Object element) {
        return ResourceLoader.getString("node." + ((NodeType) element).getKey() + ".text");
    }
}
