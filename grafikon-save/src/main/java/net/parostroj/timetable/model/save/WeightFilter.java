package net.parostroj.timetable.model.save;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Weight filter.
 *
 * @author jub
 */
public class WeightFilter implements TrainDiagramFilter {

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) {
        // fix weight info
        for (Train train : diagram.getTrains()) {
            Integer weight = TrainsHelper.getWeightFromInfoAttribute(train);
            if (weight != null)
                train.setAttribute(Train.ATTR_WEIGHT, weight);
            // remove weight.info attribute
            train.removeAttribute("weight.info");
        }
        return diagram;
    }
}
