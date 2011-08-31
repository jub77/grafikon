package net.parostroj.timetable.output2.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * Extracts information for custom cycles.
 *
 * @author jub
 */
public class CustomCyclesExtractor {

    private List<TrainsCycle> cycles;
    private AttributesExtractor attributesExtractor = new AttributesExtractor();

    public CustomCyclesExtractor(List<TrainsCycle> cycles) {
        this.cycles = cycles;
    }

    public List<CustomCycle> getCycles() {
        List<CustomCycle> outputCycles = new LinkedList<CustomCycle>();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(createCycle(cycle));
        }
        return outputCycles;
    }

    private CustomCycle createCycle(TrainsCycle cycle) {
        CustomCycle outputCycle = new CustomCycle();
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        outputCycle.setType(cycle.getType().getName());
        outputCycle.setAttributes(attributesExtractor.extract(cycle.getAttributes()));
        Iterator<TrainsCycleItem> i = cycle.getItems().iterator();
        TrainsCycleItem current = null;
        TrainsCycleItem previous = null;
        while (i.hasNext()) {
            current = i.next();
            outputCycle.getRows().add(createRow(current, previous));
            previous = current;
        }
        return outputCycle;
    }

    private CustomCycleRow createRow(TrainsCycleItem current, TrainsCycleItem previous) {
        CustomCycleRow row = new CustomCycleRow();
        row.setTrainName(current.getTrain().getName());
        row.setFromTime(TimeConverter.convertFromIntToText(current.getStartTime()));
        row.setToTime(TimeConverter.convertFromIntToText(current.getEndTime()));
        row.setFromAbbr(current.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(current.getToInterval().getOwnerAsNode().getAbbr());
        // set wait time - in real seconds (not the model ones)
        if (previous != null) {
            int time = current.getStartTime() - previous.getEndTime();
            // recalculate to real seconds
            Double timeScale = (Double)current.getTrain().getTrainDiagram().getAttribute(TrainDiagram.ATTR_TIME_SCALE);
            time = (int)Math.round((1.0d / timeScale) * time);
            row.setWait(time);
        }
        return row;
    }
}
