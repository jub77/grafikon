package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class CustomCyclesTemplateBinding extends GroovyTemplateBinding {

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> binding, TrainDiagram diagram, Locale locale) {
        // check for type
        OutputParam param = params.get("cycle_type");
        TrainsCycleType type = param != null ? param.getValue(TrainsCycleType.class) : null;
        // extract cycles
        CustomCyclesExtractor ece = new CustomCyclesExtractor(SelectionHelper.selectCycles(params, diagram, type));
        List<CustomCycle> cycles = ece.getCycles();

        // call template
        binding.put("cycles", cycles);
    }
}
