package net.parostroj.timetable.gui.views.tree;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

public class RootDelegateImpl extends CategoryDelegateImpl<TrainDiagram> {

    public RootDelegateImpl(boolean containTrains) {
        super(containTrains);
    }

    public RootDelegateImpl(boolean containTrains, TrainTreeNodeSort trainTreeNodeSort) {
        super(containTrains, trainTreeNodeSort);
    }

    @Override
    public String getNodeText(TrainDiagram item) {
        return "DIAGRAM";
    }

    @Override
    protected boolean belongs(Train train, TrainDiagram item) {
        return true;
    }

}
