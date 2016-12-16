package net.parostroj.timetable.model.ls.impl4.filters;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

public class LoadFilter4d18 implements LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter4d18.class);

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 18, 3)) <= 0) {
            // adjust complete name templates
            TextTemplate template = diagram.getTrainsData().getTrainCompleteNameTemplate();
            diagram.getTrainsData().setTrainCompleteNameTemplate(this.adjustDescription(template));
            for (TrainType type : diagram.getTrainTypes()) {
                template = type.getTrainCompleteNameTemplate();
                if (template != null) {
                    type.setTrainCompleteNameTemplate(this.adjustDescription(template));
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

    private TextTemplate adjustDescription(TextTemplate template) {
        if (template.getLanguage() == Language.GROOVY && template.getTemplate().contains("description != ''")) {
            try {
                template = TextTemplate.createTextTemplate(template.getTemplate().replace("description != ''", "description"), Language.GROOVY);
            } catch (GrafikonException e) {
                log.error("Error creating template", e);
            }
        }
        return template;
    }
}
