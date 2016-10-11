package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

public class LoadFilter4d13 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 13)) <= 0) {
            Object object = diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_SPEED_UNIT);
            if (object instanceof LengthUnit) {
                if (object == LengthUnit.KM) {
                    object = SpeedUnit.KMPH;
                } else if (object == LengthUnit.MILE) {
                    object = SpeedUnit.MPH;
                } else {
                    object = null;
                }
                diagram.getAttributes().setRemove(TrainDiagram.ATTR_EDIT_SPEED_UNIT, object);
            }
        }
    }
}
