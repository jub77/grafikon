package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class TrainsTemplateBinding extends GroovyTemplateBinding {

    private static final String[] KEY_PREFIXES = { "dc_", "trains_" };

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // title page
        boolean titlePage = true;
        if (params.paramExistWithValue("title.page"))
            titlePage = params.getParam("title.page").getValue(Boolean.class);
        String pageSort = "two_sides"; //"one_side";
        if (params.paramExistWithValue("page.sort"))
            pageSort = params.getParam("page.sort").getValue(String.class);

        // extract tts
        List<Train> trains = SelectionHelper.selectTrains(params, diagram);
        List<Route> routes = SelectionHelper.getRoutes(params, diagram, trains);
        TrainsCycle cycle = SelectionHelper.getDriverCycle(params);
        TrainTimetablesExtractor tte = new TrainTimetablesExtractor(diagram, trains, routes, cycle, locale);
        TrainTimetables timetables = tte.getTrainTimetables();

        // call template
        map.put("trains", timetables);
        map.put("title_page", titlePage);
        map.put("page_sort", pageSort);
        map.put("freight", true);
        ResourceHelper.addTextsToMap(map, KEY_PREFIXES[0], locale, LOCALIZATION_BUNDLE);
        ResourceHelper.addTextsToMap(map, KEY_PREFIXES[1], locale, LOCALIZATION_BUNDLE);
        map.put(LOCALIZATION, ResourceHelper.getBundleTranslator(LOCALIZATION_BUNDLE, KEY_PREFIXES));
    }
}
