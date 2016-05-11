package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.ResourceHelper;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class DriverCyclesTemplateBinding extends GroovyTemplateBinding {

    private static final String KEY_PREFIX = "dc_";

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // extract driver cycles
        DriverCyclesExtractor ece = new DriverCyclesExtractor(diagram, SelectionHelper.selectCycles(params, diagram,
                diagram.getDriverCycleType()), true, locale);
        DriverCycles cycles = ece.getDriverCycles();

        // call template
        map.put("cycles", cycles);
        ResourceHelper.addTextsToMap(map, KEY_PREFIX, locale, LOCALIZATION_BUNDLE);
        map.put(LOCALIZATION, ResourceHelper.getBundleTranslator(LOCALIZATION_BUNDLE, KEY_PREFIX));
    }
}
