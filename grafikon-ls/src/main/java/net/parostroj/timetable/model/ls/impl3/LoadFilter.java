package net.parostroj.timetable.model.ls.impl3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.utils.ObjectsUtil;

import java.util.Optional;

/**
 * Adjust older versions.
 *
 * @author jub
 */
public class LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter.class);

    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        log.debug("Loaded version: {}", version);
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
        // show weight info - depending on category
        for (TrainType type : diagram.getTrainTypes()) {
            if (type.getCategory().getKey().equals("freight")) {
                type.setAttribute(TrainType.ATTR_SHOW_WEIGHT_INFO, true);
            }
        }
        // localized strings
        this.convertToLocalizedStrings(diagram);

        // convert train name templates (common)
        diagram.getTrainsData()
                .setTrainNameTemplate(convertForAbbreviation(diagram.getTrainsData().getTrainNameTemplate()));
        diagram.getTrainsData().setTrainCompleteNameTemplate(
                convertForAbbreviation(diagram.getTrainsData().getTrainCompleteNameTemplate()));
        // in train types
        for (TrainType type : diagram.getTrainTypes()) {
            type.setTrainNameTemplate(convertForAbbreviation(type.getTrainNameTemplate()));
            type.setTrainCompleteNameTemplate(convertForAbbreviation(type.getTrainCompleteNameTemplate()));
        }
        // add LEFT/RIGHT connector if missing
        for (Node node : diagram.getNet().getNodes()) {
            Optional<TrackConnector> left = node.getConnectors()
                    .find(c -> c.getOrientation() == Node.Side.LEFT);
            Optional<TrackConnector> right = node.getConnectors()
                    .find(c -> c.getOrientation() == Node.Side.RIGHT);
            if (!left.isPresent()) {
                this.addConnector(diagram.getPartFactory(), node, "1", Node.Side.LEFT);
            }
            if (!right.isPresent()) {
                this.addConnector(diagram.getPartFactory(), node, "2", Node.Side.RIGHT);
            }
        }
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

    private TextTemplate convertForAbbreviation(TextTemplate template) {
        if (template != null && template.getTemplate().contains(".abbr")) {
            try {
                template = TextTemplate.createTextTemplate(template.getTemplate().replaceAll("\\.abbr", ".defaultAbbr"), template.getLanguage());
            } catch (GrafikonException e) {
                log.warn("Problem replacing abbreviation in template: {}", template.getTemplate());
            }
        }
        return template;
    }

    private void addConnector(TrainDiagramPartFactory factory, Node node, String number, Node.Side orientation) {
        TrackConnector otherConnector = factory
                .createDefaultConnector(factory.createId(), node, number, orientation, Optional.empty());
        node.getConnectors().add(otherConnector);
    }
}
