package net.parostroj.timetable.model.ls.impl4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

/**
 * Adjust older versions.
 *
 * @author jub
 */
public class LoadFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoadFilter.class);

    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 2)) <= 0) {
            // fix weight info
            for (Train train : diagram.getTrains()) {
                Integer weight = TrainsHelper.getWeightFromInfoAttribute(train);
                if (weight != null)
                    train.setAttribute(Train.ATTR_WEIGHT, weight);
                // remove weight.info attribute
                train.removeAttribute("weight.info");
            }
            // fix route info
            for (Train train : diagram.getTrains()) {
                String routeInfo = train.getAttribute("route.info", String.class);
                if (routeInfo != null)
                    routeInfo = routeInfo.trim();
                if (routeInfo != null && !"".equals(routeInfo)) {
                    try {
                        train.setAttribute(Train.ATTR_ROUTE, this.convert(routeInfo));
                    } catch (GrafikonException e) {
                        LOG.warn("Couldn't convert route info to template: {}", e.getMessage());
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
        if (version.compareTo(new ModelVersion(4, 8)) <= 0) {
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
}
