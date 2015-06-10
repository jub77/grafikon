package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class TrainUnitCyclesTemplateBinding extends GroovyTemplateBinding {

    private static final String KEY_PREFIX = "tuc_";

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // extract positions
        TrainUnitCyclesExtractor tuce = new TrainUnitCyclesExtractor(SelectionHelper.selectCycles(params, diagram,
                diagram.getTrainUnitCycleType()), locale);
        List<TrainUnitCycle> cycles = tuce.getTrainUnitCycles();

        // call template
        map.put("cycles", cycles);
        ResourceHelper.addTextsToMap(map, KEY_PREFIX, locale, LOCALIZATION_BUNDLE);
        map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram, KEY_PREFIX));
    }
}
