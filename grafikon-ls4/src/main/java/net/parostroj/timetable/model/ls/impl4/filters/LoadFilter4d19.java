package net.parostroj.timetable.model.ls.impl4.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d19 implements LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter4d19.class);

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 19, 0)) <= 0) {
            // not passing cargo in center (move to time interval from train)
            for (Train train : diagram.getTrains()) {
                if (train.getAttributeAsBool("no.transitive.region.start")) {
                    for (TimeInterval interval : train.getNodeIntervals()) {
                        if (interval.isFirst()) continue;
                        if (interval.getOwnerAsNode().isCenterOfRegions()) {
                            interval.setAttributeAsBool(TimeInterval.ATTR_NO_REGION_CENTER_TRANSFER, true);
                        }
                    }
                }
            }
        }
        if (version.compareTo(new ModelVersion(4, 19, 2)) <= 0) {
            // convert some texts to localized strings
            this.convertToLocalizedStrings(diagram);
            // filter out output templates with default.template
            removeDefaultTemplatesExceptDrawAndXml(diagram);
        }
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

    private void removeDefaultTemplatesExceptDrawAndXml(TrainDiagram diagram) {
        Collection<String> noTemplateTypes = Arrays.asList("draw", "xml");
        for (OutputTemplate template : new ArrayList<>(diagram.getOutputTemplates())) {
            Boolean defaultTemplate = template.getAttributeAsBool("default.template");
            if (defaultTemplate && !template.getOutput().equals("draw")) {
                log.warn("Skipping output template {} because of default.template feature", template.getName());
                diagram.getOutputTemplates().remove(template);
            } else {
                template.removeAttribute("default.template");
                if (noTemplateTypes.contains(template.getOutput())) {
                    template.setTemplate(null);
                }
            }
        }
    }
}
