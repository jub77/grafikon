package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class CustomCyclesTemplateBinding extends GroovyTemplateBinding {

    private static final String KEY_PREFIX = "cc_";

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // check for type
        OutputParam param = params.get("cycle_type");
        TrainsCycleType type = param != null ? param.getValue(TrainsCycleType.class) : null;
        // extract cycles
        CustomCyclesExtractor ece = new CustomCyclesExtractor(diagram, SelectionHelper.selectCycles(params, diagram, type), locale);
        List<CustomCycle> cycles = ece.getCycles();

        // call template
        map.put("cycles", cycles);
        ResourceHelper.addTextsToMap(map, KEY_PREFIX, locale, LOCALIZATION_BUNDLE);
        map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram, KEY_PREFIX));
    }
}
