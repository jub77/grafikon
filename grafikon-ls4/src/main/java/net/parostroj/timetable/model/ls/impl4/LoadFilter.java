package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Adjust older versions.
 *
 * @author jub
 */
public class LoadFilter {

    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 2)) <= 0) {
            // fix weight info
            for (Train train : diagram.getTrains()) {
                Integer weight = TrainsHelper.getWeightFromInfoAttribute(train);
                if (weight != null)
                    train.setAttribute("weight", weight);
                // remove weight.info attribute
                train.removeAttribute("weight.info");
            }
        }
    }
}
