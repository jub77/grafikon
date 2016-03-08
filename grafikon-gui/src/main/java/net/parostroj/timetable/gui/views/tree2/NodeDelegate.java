package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Train;

/**
 * Node delegate.
 *
 * @author jub
 */
public interface NodeDelegate {

    String getNodeText(TrainTreeNode trainTreeNode);

    Object getUserObject(Train train);

    boolean isNode(TrainTreeNode trainTreeNode, Train train);
}
