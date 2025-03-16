package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainType;

/**
 * Node with train type.
 *
 * @author jub
 */
public class NodeDelegateTypeImpl implements NodeDelegate {

    @Override
    public String getNodeText(TrainTreeNode trainTreeNode) {
        TrainType type = (TrainType) trainTreeNode.getUserObject();
        String result = "-";
        if (type != null) {
            LocalizedString abbr = type.getAbbr();
            LocalizedString desc = type.getDesc();
            if (abbr != null) {
                result = abbr.translate();
                if (desc != null) {
                    result += " - " + desc.translate();
                }
            }
        }
        return result;
    }

    @Override
    public Object getUserObject(Train train) {
        return train.getType();
    }

    @Override
    public boolean isNode(TrainTreeNode trainTreeNode, Train train) {
        return trainTreeNode.getUserObject() == train.getType();
    }
}
