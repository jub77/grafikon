package net.parostroj.timetable.gui.views;

import java.util.Set;
import javax.swing.tree.TreeNode;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Common interface for all nodes.
 *
 * @author jub
 */
public interface TrainTreeNode extends TreeNode {
    /**
     * returns set of trains under the node.
     *
     * @param diagram diagram
     * @return set of trains
     */
    public Set<Train> getTrains(TrainDiagram diagram);

}
