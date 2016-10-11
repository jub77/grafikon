package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;
import net.parostroj.timetable.model.units.LengthUnit;

public class LoadFilter4d21 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 21, 0)) <= 0) {
            // convert route unit...
            Attributes attributes = diagram.getAttributes();
            if (attributes.containsKey(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT)) {
                // try to convert to length unit
                String lUnitStr = attributes.get(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT, String.class);
                LengthUnit lUnit = LengthUnit.getByKey(lUnitStr);
                attributes.setRemove(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT, lUnit);
            }
        }
    }
}
