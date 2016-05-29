package net.parostroj.timetable.model.save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;

public class LocalizationFilter implements TrainDiagramFilter {

    private static final Logger log = LoggerFactory.getLogger(LocalizationFilter.class);

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException {
        log.debug("Loaded version: {}", version);
        this.convertToLocalizedStrings(diagram);
        return diagram;
    }

    private void convertToLocalizedStrings(TrainDiagram diagram) {
        // (1) convert comments
        for (Train train : diagram.getTrains()) {
            for (TimeInterval interval : train) {
                String comment = interval.getAttribute(TimeInterval.ATTR_COMMENT, String.class);
                if (comment != null) {
                    interval.setAttribute(TimeInterval.ATTR_COMMENT, LocalizedString.fromString(comment));
                }
            }
        }
        // (2) add display name property to circulation types
        for (TrainsCycleType circulationType : diagram.getCycleTypes()) {
            if (!circulationType.isDefaultType() && circulationType.getDisplayName() == null) {
                circulationType.setAttribute(TrainsCycleType.ATTR_DISPLAY_NAME,
                        LocalizedString.fromString(circulationType.getName()));
            }
        }
        // (3) descriptions of output templates
        for (OutputTemplate ot : diagram.getOutputTemplates()) {
            String desc = ot.getAttribute(OutputTemplate.ATTR_DESCRIPTION, String.class);
            if (desc != null) {
                ot.setAttribute(OutputTemplate.ATTR_DESCRIPTION, LocalizedString.fromString(desc));
            }
        }
    }
}
