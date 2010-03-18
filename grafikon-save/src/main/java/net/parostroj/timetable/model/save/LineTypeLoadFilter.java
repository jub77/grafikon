package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import java.util.logging.Logger;
import net.parostroj.timetable.model.*;

/**
 * Changing D3/D2 values to line.controlled boolean value.
 * 
 * @author jub
 */
public class LineTypeLoadFilter implements TrainDiagramFilter {
    
    private static final Logger LOG = Logger.getLogger(LineTypeLoadFilter.class.getName());

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException {
        if (version.getMajorVersion() <= 2) {
            for (Line line : diagram.getNet().getLines()) {
                line.setAttribute("line.controlled", "D3".equals(line.getAttribute("line.type")));
                line.removeAttribute("line.type");
            }
        }
        return diagram;
    }
}
