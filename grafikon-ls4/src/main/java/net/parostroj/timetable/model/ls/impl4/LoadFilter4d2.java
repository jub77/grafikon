package net.parostroj.timetable.model.ls.impl4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.utils.ObjectsUtil;

public class LoadFilter4d2 implements LoadFilter {

    private static final Logger log = LoggerFactory.getLogger(LoadFilter4d2.class);

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
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
