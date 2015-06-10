package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class StationsTemplateBinding extends GroovyTemplateBinding {

    private static final String KEY_PREFIX = "stations_";

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // show circulations in adjacent sessions
        boolean adjacentSessions = params.getParamValue("adjacent.sessions", Boolean.class, false);

        // technological time
        boolean techTime = false;
        if (params.paramExistWithValue("tech.time")) {
            techTime = params.getParam("tech.time").getValue(Boolean.class);
        }
        // extract positions
        StationTimetablesExtractor se = new StationTimetablesExtractor(diagram,
                SelectionHelper.selectNodes(params, diagram), techTime, adjacentSessions, locale);
        List<StationTimetable> timetables = se.getStationTimetables();

        // call template
        map.put("stations", timetables);
        map.put("adjacent_sessions", adjacentSessions);
        ResourceHelper.addTextsToMap(map, KEY_PREFIX, locale, LOCALIZATION_BUNDLE);
        map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram, KEY_PREFIX));
    }
}
