package net.parostroj.timetable.output2.groovy;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.Cycles;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;

public class StartsTemplateBinding extends GroovyTemplateBinding {

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        Integer startTime = params.getParamValue("start.time", Integer.class);
        // extract positions
        PositionsExtractor pe = new PositionsExtractor(diagram);
        List<Position> engines = pe.getStartPositions(diagram.getEngineCycleType().getCycles(), startTime);
        List<Position> trainUnits = pe.getStartPositions(diagram.getTrainUnitCycleType().getCycles(), startTime);
        List<Cycles> customCycles = pe.getStartPositionsCustom(startTime);

        // call template
        map.put("engines", engines);
        map.put("train_units", trainUnits);
        map.put("custom_cycles", customCycles);
        map.put("start_time", startTime);
    }
}
