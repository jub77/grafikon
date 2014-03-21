package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;

/**
 * Delegate for types of trains.
 *
 * @author jub
 */
public class ChildrenDelegateTypesImpl extends ChildrenDelegateImpl implements ChildrenDelegate {

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
        TrainType newType = this.getTypeFromNode(newChildNode);
        if (newType != null) {
            TrainDiagram diagram = newType.getTrainDiagram();
            for (int i = 0; i < node.getChildCount(); i++) {
                TrainTreeNode childNode = (TrainTreeNode) node.getChildAt(i);
                if (this.compareTypes(diagram, this.getTypeFromNode(newChildNode), this.getTypeFromNode(childNode)) < 0) {
                    node.insert(newChildNode, i);
                    return i;
                }
            }
        }
        node.add(newChildNode);
        return node.getChildCount() - 1;
    }

    private int compareTypes(TrainDiagram diagram, TrainType type1, TrainType type2) {
        int i1 = diagram.getTrainTypes().indexOf(type1);
        int i2 = diagram.getTrainTypes().indexOf(type2);
        return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
    }

    private TrainType getTypeFromNode(TrainTreeNode node) {
        return (TrainType) node.getUserObject();
    }
}
