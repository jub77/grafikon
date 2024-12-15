package net.parostroj.timetable.model.ls.impl4.filters;

import java.util.Collections;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d18 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 18, 3)) <= 0) {
            TrainDiagramType diagramType = diagram.getType();
            // adjust complete name templates
            TextTemplate template = diagram.getTrainsData().getTrainCompleteNameTemplate();
            diagram.getTrainsData().setTrainCompleteNameTemplate(this.adjustDescription(template, diagramType));
            for (TrainType type : diagram.getTrainTypes()) {
                template = type.getTrainCompleteNameTemplate();
                if (template != null) {
                    type.setTrainCompleteNameTemplate(this.adjustDescription(template, diagramType));
                }
            }
        }
        if (version.compareTo(new ModelVersion(4, 18, 4)) <= 0) {
            // multiple regions allowed per node (even centers)
            for (Node node : diagram.getNet().getNodes()) {
                Region region = (Region) node.removeAttribute("region");
                if (region != null) {
                    Boolean regionCenter = (Boolean) node.removeAttribute("region.start");
                    node.setAttribute(Node.ATTR_REGIONS, Collections.singleton(region));
                    if (regionCenter != null && regionCenter) {
                        node.setAttribute(Node.ATTR_CENTER_OF_REGIONS, Collections.singleton(region));
                    }
                }
            }
        }
    }

    private TextTemplate adjustDescription(TextTemplate template, TrainDiagramType type) {
        if (template.getLanguage() == Language.GROOVY && template.getTemplate().contains("description != ''")) {
            template = type.createTextTemplate(template.getTemplate().replace("description != ''", "description"), Language.GROOVY);
        }
        return template;
    }
}
