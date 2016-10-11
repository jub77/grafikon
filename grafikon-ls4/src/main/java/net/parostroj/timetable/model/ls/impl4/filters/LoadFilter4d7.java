package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d7 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 7)) <= 0) {
            // show weight info - depending on category
            for (TrainType type : diagram.getTrainTypes()) {
                if (type.getCategory().getKey().equals("freight")) {
                    type.setAttribute(TrainType.ATTR_SHOW_WEIGHT_INFO, true);
                }
            }
        }
    }
}
