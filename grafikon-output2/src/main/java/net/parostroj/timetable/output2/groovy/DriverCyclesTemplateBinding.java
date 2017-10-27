package net.parostroj.timetable.output2.groovy;

import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.*;
import net.parostroj.timetable.output2.util.SelectionHelper;

public class DriverCyclesTemplateBinding extends GroovyTemplateBinding {

    @Override
    protected void addSpecific(OutputParams params, Map<String, Object> map, TrainDiagram diagram, Locale locale) {
        // extract driver cycles
        DriverCyclesExtractor ece = new DriverCyclesExtractor(diagram, SelectionHelper.selectCycles(params, diagram,
                diagram.getDriverCycleType()), true);
        DriverCycles cycles = ece.getDriverCycles();

        // call template
        map.put("cycles", cycles);
    }
}
