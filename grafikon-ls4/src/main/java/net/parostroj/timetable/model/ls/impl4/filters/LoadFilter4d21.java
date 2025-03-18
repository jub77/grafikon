package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;
import net.parostroj.timetable.model.units.LengthUnit;

public class LoadFilter4d21 implements LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter4d21.class);

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
        if (version.compareTo(new ModelVersion(4, 21, 1)) <= 0) {
            Permissions permissions = diagram.getRuntimeInfo().getPermissions();
            // convert train name templates (common)
            diagram.getTrainsData()
                    .setTrainNameTemplate(convertForAbbreviation(diagram.getTrainsData().getTrainNameTemplate(), permissions));
            diagram.getTrainsData().setTrainCompleteNameTemplate(
                    convertForAbbreviation(diagram.getTrainsData().getTrainCompleteNameTemplate(), permissions));
            // in train types
            for (TrainType type : diagram.getTrainTypes()) {
                type.setTrainNameTemplate(convertForAbbreviation(type.getTrainNameTemplate(), permissions));
                type.setTrainCompleteNameTemplate(convertForAbbreviation(type.getTrainCompleteNameTemplate(), permissions));
            }
        }
    }

    private TextTemplate convertForAbbreviation(TextTemplate template, Permissions permissions) {
        if (template != null && template.getTemplate().contains(".abbr") && template.getLanguage() != TextTemplate.Language.SIMPLE) {
            try {
                template = permissions.createTextTemplate(
                        template.getTemplate().replaceAll("\\.abbr", ".defaultAbbr"), template.getLanguage());
            } catch (GrafikonException e) {
                log.warn("Problem replacing abbreviation in template: {}", template.getTemplate());
            }
        }
        return template;
    }
}
