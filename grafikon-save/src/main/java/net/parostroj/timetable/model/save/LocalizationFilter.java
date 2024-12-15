package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;

public class LocalizationFilter implements TrainDiagramFilter {

    private static final Logger log = LoggerFactory.getLogger(LocalizationFilter.class);

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException {
        log.debug("Loaded version: {}", version);
        this.convertToLocalizedStrings(diagram);

        TrainDiagramType diagramType = diagram.getType();
        // convert train name templates (common)
        diagram.getTrainsData()
                .setTrainNameTemplate(convertForAbbreviation(diagram.getTrainsData().getTrainNameTemplate(), diagramType));
        diagram.getTrainsData().setTrainCompleteNameTemplate(
                convertForAbbreviation(diagram.getTrainsData().getTrainCompleteNameTemplate(), diagramType));
        // in train types
        for (TrainType type : diagram.getTrainTypes()) {
            type.setTrainNameTemplate(convertForAbbreviation(type.getTrainNameTemplate(), diagramType));
            type.setTrainCompleteNameTemplate(convertForAbbreviation(type.getTrainCompleteNameTemplate(), diagramType));
        }

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
        // (2) add translations for default types
        for (TrainsCycleType circulationType : diagram.getCycleTypes()) {
            if (circulationType.isDefaultType()) {
                circulationType.setName(TrainsCycleType.getNameForDefaultType(circulationType.getKey()));
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

    private TextTemplate convertForAbbreviation(TextTemplate template, TrainDiagramType diagramType) {
        if (template != null && template.getTemplate().contains(".abbr")) {
            try {
                template = diagramType.createTextTemplate(template.getTemplate().replaceAll("\\.abbr", ".defaultAbbr"), template.getLanguage());
            } catch (GrafikonException e) {
                log.warn("Problem replacing abbreviation in template: {}", template.getTemplate());
            }
        }
        return template;
    }
}
