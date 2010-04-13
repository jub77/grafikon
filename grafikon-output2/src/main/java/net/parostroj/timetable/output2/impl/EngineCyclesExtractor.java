package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * Extracts information for train unit cycles.
 *
 * @author jub
 */
public class EngineCyclesExtractor {

    private List<TrainsCycle> cycles;

    public EngineCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles) {
        this.cycles = cycles;
    }

    public List<EngineCycle> getEngineCycles() {
        List<EngineCycle> outputCycles = new LinkedList<EngineCycle>();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(createCycle(cycle));
        }
        return outputCycles;
    }

    private EngineCycle createCycle(TrainsCycle cycle) {
        EngineCycle outputCycle = new EngineCycle();
        return outputCycle;
    }
}
