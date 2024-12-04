package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d26 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 26, 1)) <= 0) {
            diagram.getTextItems().forEach(item -> item.getAttributes().remove("train.timetable.info"));
        }
    }
}
