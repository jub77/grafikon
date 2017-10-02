package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d24 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 24, 0)) <= 0) {
            // remove previous joined train (only next is kept)
            for (Train train : diagram.getTrains()) {
                train.getAttributes().setSkipListeners(true);
                try {
                    train.removeAttribute(Train.ATTR_PREVIOUS_JOINED_TRAIN);
                } finally {
                    train.getAttributes().setSkipListeners(false);
                }
            }
        }
    }
}
