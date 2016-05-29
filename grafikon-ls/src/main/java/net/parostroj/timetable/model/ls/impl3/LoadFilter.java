package net.parostroj.timetable.model.ls.impl3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Adjust older versions.
 *
 * @author jub
 */
public class LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter.class);

    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
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
