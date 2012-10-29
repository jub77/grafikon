package net.parostroj.timetable.gui.views.tree2;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.filters.Filter;
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

    public TrainTreeHandler getFlatHandler(Filter<Train> filter, TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<Pair<NodeDelegate,ChildrenDelegate>>();
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateRootImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        TrainTreeHandler handler = new TrainTreeHandler(structure, filter);
        return handler;
    }

    public TrainTreeHandler getTypesHandler(Filter<Train> filter, TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<Pair<NodeDelegate,ChildrenDelegate>>();
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateRootImpl(), new ChildrenDelegateTypesImpl()));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateTypeImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        TrainTreeHandler handler = new TrainTreeHandler(structure, filter);
        return handler;
    }

    public TrainTreeHandler getGroupsHandler(TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<Pair<NodeDelegate,ChildrenDelegate>>();
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateRootImpl(), new ChildrenDelegateGroupsImpl()));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateGroupImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        TrainTreeHandler handler = new TrainTreeHandler(structure, null);
        return handler;
    }

    public TrainTreeHandler getGroupsAndTypesHandler(TrainDiagram diagram) {
        List<Pair<NodeDelegate, ChildrenDelegate>> structure = new LinkedList<Pair<NodeDelegate,ChildrenDelegate>>();
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateRootImpl(), new ChildrenDelegateGroupsImpl()));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateGroupImpl(), new ChildrenDelegateTypesImpl()));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateTypeImpl(), new ChildrenDelegateTrainsImpl(diagram)));
        structure.add(new Pair<NodeDelegate, ChildrenDelegate>(new NodeDelegateTrainImpl(), new ChildrenDelegateEmptyImpl()));
        TrainTreeHandler handler = new TrainTreeHandler(structure, null);
        return handler;
    }
}
