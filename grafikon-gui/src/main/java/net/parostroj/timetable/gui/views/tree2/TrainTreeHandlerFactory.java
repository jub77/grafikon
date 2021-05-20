package net.parostroj.timetable.gui.views.tree2;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;

/**
 * Factory for tree handlers.
 *
 * @author jub
 */
public class TrainTreeHandlerFactory {

    private static final TrainTreeHandlerFactory INSTANCE = new TrainTreeHandlerFactory();

    public static TrainTreeHandlerFactory getInstance() {
        return INSTANCE;
    }

    public TrainTreeHandler getFlatHandler(Predicate<Train> filter, TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<>();
        structure.add(new Pair<>(new NodeDelegateRootImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        return new TrainTreeHandler(structure, filter);
    }

    public TrainTreeHandler getTypesHandler(Predicate<Train> filter, TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<>();
        structure.add(new Pair<>(new NodeDelegateRootImpl(), new ChildrenDelegateTypesImpl()));
        structure.add(new Pair<>(new NodeDelegateTypeImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        return new TrainTreeHandler(structure, filter);
    }

    public TrainTreeHandler getGroupsHandler(TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<>();
        structure.add(new Pair<>(new NodeDelegateRootImpl(), new ChildrenDelegateGroupsImpl()));
        structure.add(new Pair<>(new NodeDelegateGroupImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        return new TrainTreeHandler(structure, null);
    }

    public TrainTreeHandler getGroupsAndTypesHandler(TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<>();
        structure.add(new Pair<>(new NodeDelegateRootImpl(), new ChildrenDelegateGroupsImpl()));
        structure.add(new Pair<>(new NodeDelegateGroupImpl(), new ChildrenDelegateTypesImpl()));
        structure.add(new Pair<>(new NodeDelegateTypeImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        return new TrainTreeHandler(structure, null);
    }
}
