package net.parostroj.timetable.gui.views.tree2;

import java.util.*;

import javax.swing.tree.DefaultTreeModel;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.Pair;

/**
 * Handles tree.
 *
 * @author jub
 */
public class TrainTreeHandler {

    private final List<Pair<NodeDelegate, ChildrenDelegate>> structure;
    private final Filter<Train> filter;
    private final DefaultTreeModel treeModel;

    public TrainTreeHandler(List<Pair<NodeDelegate, ChildrenDelegate>> structure, Filter<Train> filter) {
        this.structure = structure;
        this.filter = filter;
        this.treeModel = new DefaultTreeModel(this.createNode(structure.get(0), null));
    }

    public void addTrains(Collection<Train> trains) {
        for (Train train : trains) {
            this.addTrain(train);
        }
    }

    public TrainTreeNode addTrain(Train train) {
        if (filter != null && !filter.is(train))
            return null;
        TrainTreeNode root = (TrainTreeNode) treeModel.getRoot();
        TrainTreeNode node = root;
        Iterator<Pair<NodeDelegate, ChildrenDelegate>> i = structure.iterator();
        i.next();
        while (i.hasNext()) {
            Pair<NodeDelegate,ChildrenDelegate> pair = i.next();
            TrainTreeNode nextNode = node.getChildNode(train);
            if (nextNode == null) {
                // create node ...
                nextNode = this.createNode(pair, train);
                int index = node.addChildNode(nextNode);
                treeModel.nodesWereInserted(node, new int[] { index });
            }
            node = nextNode;
        }
        return node;
    }

    public TrainTreeNode removeTrain(Train train) {
        TrainTreeNode node = this.getTrain(train);
        if (node != null) {
            TrainTreeNode parent = (TrainTreeNode) node.getParent();
            this.removeFromParentAndEvent(node);
            while (parent.getParent() != null) {
                TrainTreeNode nextParent = (TrainTreeNode) parent.getParent();
                if (parent.getChildCount() == 0) {
                    this.removeFromParentAndEvent(parent);
                }
                parent = nextParent;
            }
            return (TrainTreeNode) node.getParent();
        } else {
            return null;
        }
    }

    private void removeFromParentAndEvent(TrainTreeNode node) {
        TrainTreeNode parent = (TrainTreeNode) node.getParent();
        int index = parent.getIndex(node);
        node.removeFromParent();
        treeModel.nodesWereRemoved(parent, new int[] { index }, new Object[] { node });
    }

    public TrainTreeNode getTrain(Train train) {
        return this.getTrainImpl((TrainTreeNode) treeModel.getRoot(), train);
    }

    private TrainTreeNode getTrainImpl(TrainTreeNode node, Train train) {
        if (node.getUserObject() == train)
            return node;
        else {
            for (TrainTreeNode childNode : getIterable(node.children(), TrainTreeNode.class)) {
                TrainTreeNode found = this.getTrainImpl(childNode, train);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    public Collection<Train> getTrains(TrainTreeNode node) {
        Set<Train> trains = new HashSet<Train>();
        this.getTrains(trains, node);
        return trains;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    protected TrainTreeNode createNode(Pair<NodeDelegate, ChildrenDelegate> pair, Train train) {
        return new TrainTreeNode(train, pair.first, pair.second);
    }

    private void getTrains(Collection<Train> trains, TrainTreeNode node) {
        if (node.getUserObject() instanceof Train) {
            trains.add((Train) node.getUserObject());
        }
        for (TrainTreeNode childNode : getIterable(node.children(), TrainTreeNode.class)) {
            this.getTrains(trains, childNode);
        }
    }

    static <T> Iterable<T> getIterable(final Enumeration<?> e, final Class<T> clazz) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    @Override
                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    @Override
                    public T next() {
                        return clazz.cast(e.nextElement());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
