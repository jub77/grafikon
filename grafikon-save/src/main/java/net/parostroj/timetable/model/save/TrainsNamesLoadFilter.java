package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converting old style of train names into the new one ... Effectivelly
 * stripping all other stuff except for train number and adding templates
 * and sort pattern.
 * 
 * @author jub
 */
public class TrainsNamesLoadFilter implements TrainDiagramFilter {
    
    private static final Logger LOG = LoggerFactory.getLogger(TrainsNamesLoadFilter.class.getName());

    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) throws LSException {
        if (version.getMajorVersion() <= 2 && version.getMinorVersion() < 1) {
            Pair<TrainsData, List<TrainType>> defaultList = DefaultTrainTypeListSource.getDefaultTypeList();
            // add train sort pattern ...
            if (diagram.getTrainsData().getTrainSortPattern() == null)
                diagram.getTrainsData().setTrainSortPattern(defaultList.first.getTrainSortPattern());
            // set default train name templates
            if (diagram.getTrainsData().getTrainNameTemplate() == null)
                diagram.getTrainsData().setTrainNameTemplate(defaultList.first.getTrainNameTemplate());
            if (diagram.getTrainsData().getTrainCompleteNameTemplate() == null)
                diagram.getTrainsData().setTrainCompleteNameTemplate(defaultList.first.getTrainCompleteNameTemplate());
            // do conversion (remove everything but train number) ...
            Pattern conversionPattern = Pattern.compile("\\D+(\\d+.*)");
            // convert name of all trains
            for (Train train : diagram.getTrains()) {
                try {
                    Matcher m = conversionPattern.matcher(train.getNumber());
                    if (m.matches()) {
                        train.setNumber(m.group(1));
                    } else {
                        LOG.warn("Cannot convert train name. Name doesn't match: {}", train.getNumber());
                    }
                } catch (Exception e) {
                    LOG.warn("Cannot convert train name.", e);
                }
            }
        }
        return diagram;
    }
}
