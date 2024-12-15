package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Recalculate trains to correct running times.
 *
 * @author jub
 */
public class RecalculateFilter implements TrainDiagramFilter {

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) {
        for (Train train : diagram.getTrains()) {
            train.recalculate();
        }
        return diagram;
    }
}
