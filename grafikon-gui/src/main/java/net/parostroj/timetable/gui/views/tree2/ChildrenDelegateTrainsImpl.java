package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Delegate for trains.
 *
 * @author jub
 */
public class ChildrenDelegateTrainsImpl extends ChildrenDelegateImpl implements ChildrenDelegate {

    private final TrainComparator comparator;

    public ChildrenDelegateTrainsImpl(TrainDiagram diagram) {
        comparator = new TrainComparator(TrainComparator.Type.ASC, diagram.getTrainsData().getTrainSortPattern());
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public int addChildNode(TrainTreeNode node, TrainTreeNode newChildNode) {
        for (int i = 0; i < node.getChildCount(); i++) {
            TrainTreeNode childNode = (TrainTreeNode) node.getChildAt(i);
            if (comparator.compare(this.getTrainFromNode(newChildNode), this.getTrainFromNode(childNode)) < 0) {
                node.insert(newChildNode, i);
                return i;
            }
        }
        node.add(newChildNode);
        return node.getChildCount() - 1;
    }

    private Train getTrainFromNode(TrainTreeNode node) {
        return (Train) node.getUserObject();
    }
}
