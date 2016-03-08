package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Train;

/**
 * Root node delegate.
 *
 * @author jub
 */
public class NodeDelegateRootImpl implements NodeDelegate {

    @Override
    public String getNodeText(TrainTreeNode trainTreeNode) {
        return "";
    }

    @Override
    public Object getUserObject(Train train) {
        return null;
    }

    @Override
    public boolean isNode(TrainTreeNode trainTreeNode, Train train) {
        return true;
    }
}
