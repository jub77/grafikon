package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Filter applied after loading diagram.
 *
 * @author jub
 */
public interface LoadFilter {

    void checkDiagram(TrainDiagram diagram, ModelVersion version);

}