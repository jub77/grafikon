package net.parostroj.timetable.gui.views.tree;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;

/**
 * Factory.
 *
 * @author jub
 */
public class TrainTreeNodeFactory {

    private static final TrainTreeNodeFactory INSTANCE = new TrainTreeNodeFactory();

    public static TrainTreeNodeFactory getInstance() {
        return INSTANCE;
    }

    public TrainTreeNode<TrainDiagram> createFlatTree(TrainDiagram diagram) {
        TrainTreeNode<TrainDiagram> rootNode = createRootNode(diagram, true);
        for (Train train : diagram.getTrains()) {
            rootNode.addTrain(train);
        }
        return rootNode;
    }

    public TrainTreeNode<TrainDiagram> createTypeTree(TrainDiagram diagram) {
        TrainTreeNode<TrainDiagram> rootNode = createRootNode(diagram, false);
        for (TrainType trainType : diagram.getTrainTypes()) {
            TrainTreeNode<TrainType> typeNode = createTrainTypeNode(rootNode, trainType);
            rootNode.getChildren().add(typeNode);
        }
        for (Train train : diagram.getTrains()) {
            rootNode.addTrain(train);
        }
        return rootNode;
    }

    public TrainTreeNode<Train> createTrainNode(TrainTreeNode<?> parent, Train train) {
        return new TrainTreeNodeImpl<Train>(parent, new TrainDelegateImpl(), train);
    }

    public TrainTreeNode<TrainType> createTrainTypeNode(TrainTreeNode<?> parent, TrainType type) {
        return new TrainTreeNodeImpl<TrainType>(parent, new TrainTypeDelegateImpl(true, createSort(type.getTrainDiagram())), type);
    }

    public TrainTreeNode<TrainDiagram> createRootNode(TrainDiagram diagram, boolean containTrains) {
        return new TrainTreeNodeImpl<TrainDiagram>(null, new RootDelegateImpl(containTrains, containTrains ? createSort(diagram) : null), diagram);
    }

    public TrainTreeNodeSort createSort(TrainDiagram diagram) {
        return new TrainTreeNodeSortImpl(new TrainComparator(TrainComparator.Type.ASC, diagram.getTrainsData().getTrainSortPattern()));
    }
}
