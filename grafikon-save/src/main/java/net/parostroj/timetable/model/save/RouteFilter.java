package net.parostroj.timetable.model.save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Route filter.
 *
 * @author jub
 */
public class RouteFilter implements TrainDiagramFilter {

    private static final Logger log = LoggerFactory.getLogger(RouteFilter.class);

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException {
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
        return diagram;
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
        return TextTemplate.createTextTemplate(result.toString(), TextTemplate.Language.GROOVY);
    }
}
