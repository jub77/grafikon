package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.*;

/**
 * Changing D3/D2 values to line.controlled boolean value.
 *
 * @author jub
 */
public class LineTypeLoadFilter implements TrainDiagramFilter {

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException {
        if (version.getMajorVersion() <= 2) {
            for (Line line : diagram.getNet().getLines()) {
                line.setAttribute(Line.ATTR_CONTROLLED, "D3".equals(line.getAttribute("line.type", String.class)));
                line.removeAttribute("line.type");
            }
        }
        return diagram;
    }
}
