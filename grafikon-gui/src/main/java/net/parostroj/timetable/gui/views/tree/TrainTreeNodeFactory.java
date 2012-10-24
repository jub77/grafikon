package net.parostroj.timetable.gui.views.tree;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.model.*;

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

    public TrainTreeNode<TrainDiagram> createFlatTree(TrainDiagram diagram, Filter<Train> filter) {
        TrainTreeNode<TrainDiagram> rootNode = createRootNode(diagram, true, filter);
        for (Train train : diagram.getTrains()) {
            rootNode.addTrain(train);
        }
        return rootNode;
    }

    public TrainTreeNode<TrainDiagram> createTypeTree(TrainDiagram diagram, Filter<Train> filter) {
        TrainTreeNode<TrainDiagram> rootNode = createRootNode(diagram, false, filter);
        for (TrainType trainType : diagram.getTrainTypes()) {
            TrainTreeNode<TrainType> typeNode = createTrainTypeNode(rootNode, trainType);
            rootNode.getChildren().add(typeNode);
        }
        for (Train train : diagram.getTrains()) {
            rootNode.addTrain(train);
        }
        return rootNode;
    }

    public TrainTreeNode<TrainDiagram> createGroupTree(TrainDiagram diagram) {
        TrainTreeNode<TrainDiagram> rootNode = createRootNode(diagram, false, null);
        List<Group> groups = new LinkedList<Group>();
        groups.add(null);
        groups.addAll(diagram.getGroups());
        for (Group group : groups) {
            TrainTreeNode<Group> groupNode = createGroupNode(rootNode, group, false);
            rootNode.getChildren().add(groupNode);
            for (TrainType trainType : diagram.getTrainTypes()) {
                TrainTreeNode<TrainType> typeNode = createTrainTypeNode(groupNode, trainType);
                groupNode.getChildren().add(typeNode);
            }
        }
        for (Train train : diagram.getTrains()) {
            rootNode.addTrain(train);
        }
        return rootNode;
    }

    public TrainTreeNode<TrainDiagram> createGroupFlatTree(TrainDiagram diagram) {
        TrainTreeNode<TrainDiagram> rootNode = createRootNode(diagram, false, null);
        List<Group> groups = new LinkedList<Group>();
        groups.add(null);
        groups.addAll(diagram.getGroups());
        for (Group group : groups) {
            TrainTreeNode<Group> groupNode = createGroupNode(rootNode, group, true);
            rootNode.getChildren().add(groupNode);
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

    public TrainTreeNode<Group> createGroupNode(TrainTreeNode<?> parent, Group group, boolean containsTrains) {
        return new TrainTreeNodeImpl<Group>(parent, new GroupDelegateImpl(containsTrains), group);
    }

    public TrainTreeNode<TrainDiagram> createRootNode(TrainDiagram diagram, boolean containTrains, Filter<Train> filter) {
        return new TrainTreeNodeImpl<TrainDiagram>(null, new RootDelegateImpl(containTrains, containTrains ? createSort(diagram) : null, filter), diagram);
    }

    public TrainTreeNodeSort createSort(TrainDiagram diagram) {
        return new TrainTreeNodeSortImpl(new TrainComparator(TrainComparator.Type.ASC, diagram.getTrainsData().getTrainSortPattern()));
    }
}
