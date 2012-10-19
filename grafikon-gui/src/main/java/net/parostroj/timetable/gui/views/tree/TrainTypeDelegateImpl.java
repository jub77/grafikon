package net.parostroj.timetable.gui.views.tree;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainType;

public class TrainTypeDelegateImpl extends CategoryDelegateImpl<TrainType> {

    public TrainTypeDelegateImpl(boolean containTrains) {
        super(containTrains);
    }

    public TrainTypeDelegateImpl(boolean containTrains, TrainTreeNodeSort sort) {
        super(containTrains, sort);
    }

    @Override
    public String getNodeText(TrainType item) {
        return item.getDesc();
    }

    @Override
    protected boolean belongs(Train train, TrainType item) {
        return train.getType() == item;
    }
}
