package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.Cycles;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;

public class EndsTemplateBinding extends GroovyTemplateBinding {

    private static final String KEY_PREFIX = "end_positions_";

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // extract positions
        PositionsExtractor pe = new PositionsExtractor(diagram);
        List<Position> engines = pe.getEndPositions(diagram.getEngineCycles(), null);
        List<Position> trainUnits = pe.getEndPositions(diagram.getTrainUnitCycles(), null);
        List<Cycles> customCycles = pe.getEndPositionsCustom(null);

        // call template
        map.put("engines", engines);
        map.put("train_units", trainUnits);
        map.put("custom_cycles", customCycles);
        ResourceHelper.addTextsToMap(map, KEY_PREFIX, locale, LOCALIZATION_BUNDLE);
        map.put(TRANSLATOR, ResourceHelper.getTranslator(LOCALIZATION_BUNDLE, diagram, KEY_PREFIX));
    }
}
