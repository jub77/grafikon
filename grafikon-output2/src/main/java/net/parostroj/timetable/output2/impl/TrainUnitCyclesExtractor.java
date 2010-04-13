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
public class TrainUnitCyclesExtractor {

    private List<TrainsCycle> cycles;

    public TrainUnitCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles) {
        this.cycles = cycles;
    }

    public List<TrainUnitCycle> getTrainUnitCycles() {
        List<TrainUnitCycle> outputCycles = new LinkedList<TrainUnitCycle>();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(createCycle(cycle));
        }
        return outputCycles;
    }

    private TrainUnitCycle createCycle(TrainsCycle cycle) {
        TrainUnitCycle outputCycle = new TrainUnitCycle();
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        for (TrainsCycleItem item : cycle.getItems()) {
            outputCycle.getRows().add(createRow(item));
        }
        return outputCycle;
    }

    private TrainUnitCycleRow createRow(TrainsCycleItem item) {
        TrainUnitCycleRow row = new TrainUnitCycleRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(TimeConverter.convertFromIntToText(item.getStartTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        row.setComment((item.getComment() == null || item.getComment().trim().equals("")) ? null : item.getComment());
        return row;
    }
}
