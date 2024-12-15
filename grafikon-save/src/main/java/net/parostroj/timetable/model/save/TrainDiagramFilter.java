package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.*;

/**
 * Filter for train diagram.
 *
 * @author jub
 */
public interface TrainDiagramFilter {
    TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException;
}
