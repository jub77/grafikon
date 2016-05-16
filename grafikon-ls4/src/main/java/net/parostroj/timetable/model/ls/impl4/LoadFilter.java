package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Adjust older versions.
 *
 * @author jub
 */
public class LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter.class);

    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        log.debug("Loaded version: {}", version);
        if (version.compareTo(new ModelVersion(4, 2)) <= 0) {
            // fix weight info
            for (Train train : diagram.getTrains()) {
                Integer weight = TrainsHelper.getWeightFromInfoAttribute(train);
                if (weight != null) {
                    train.setAttribute(Train.ATTR_WEIGHT, weight);
                }
                // remove weight.info attribute
                train.removeAttribute("weight.info");
            }
            // fix route info
            for (Train train : diagram.getTrains()) {
                String routeInfo = ObjectsUtil.checkAndTrim(train.getAttribute("route.info", String.class));
                if (routeInfo != null) {
                    try {
                        train.setAttribute(Train.ATTR_ROUTE, this.convert(routeInfo));
                    } catch (GrafikonException e) {
                        log.warn("Couldn't convert route info to template: {}", e.getMessage());
                    }
                    train.removeAttribute("route.info");
                }
            }
        }
        if (version.compareTo(new ModelVersion(4, 7)) <= 0) {
            // show weight info - depending on category
            for (TrainType type : diagram.getTrainTypes()) {
                if (type.getCategory().getKey().equals("freight")) {
                    type.setAttribute(TrainType.ATTR_SHOW_WEIGHT_INFO, true);
                }
            }
        }
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
                    node.setAttribute(Node.ATTR_REGIONS, Collections.singletonList(region));
                    if (regionCenter != null && regionCenter) {
                        node.setAttribute(Node.ATTR_CENTER_OF_REGIONS, Collections.singletonList(region));
                    }
                }
            }
        }
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

    private TextTemplate convert(String routeInfo) throws GrafikonException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < routeInfo.length(); i++) {
            char ch = routeInfo.charAt(i);
            if (ch == '$') {
                char nextCh = (i + 1) < routeInfo.length() ? routeInfo.charAt(i + 1) : ' ';
                switch (nextCh) {
                    case '1':
                        result.append("${stations.first}");
                        i++;
                        break;
                    case '2':
                        result.append("${stations.last}");
                        i++;
                        break;
                    default:
                        result.append("${stations.first} - ${stations.last}");
                        break;
                }
            } else {
                result.append(ch);
            }
        }
        return TextTemplate.createTextTemplate(result.toString(), TextTemplate.Language.MVEL);
    }

    private void convertToLocalizedStrings(TrainDiagram diagram) {
        // (1) convert comments
        for (Train train : diagram.getTrains()) {
            for (TimeInterval interval : train) {
                String comment = interval.getAttribute(TimeInterval.ATTR_COMMENT, String.class);
                if (comment != null) {
                    interval.setAttribute(TimeInterval.ATTR_COMMENT, LocalizedString.newBuilder(comment).build());
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
