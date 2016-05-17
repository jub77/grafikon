package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class TrainsTemplateBinding extends GroovyTemplateBinding {

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // extract tts
        List<Train> trains = SelectionHelper.selectTrains(params, diagram);
        List<Route> routes = SelectionHelper.getRoutes(params, diagram, trains);
        TrainsCycle cycle = SelectionHelper.getDriverCycle(params);
        TrainTimetablesExtractor tte = new TrainTimetablesExtractor(diagram, trains, routes, cycle, locale);
        TrainTimetables timetables = tte.getTrainTimetables();

        // call template
        map.put("trains", timetables);
    }
}
