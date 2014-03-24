package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.NodeType;

/**
 * NodeType wrapper delegate.
 *
 * @author jub
 */
public class NodeTypeWrapperDelegate implements WrapperDelegate {

    @Override
    public String toString(Object element) {
        return ResourceLoader.getString("node." + ((NodeType) element).getKey() + ".text");
    }

    @Override
    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
