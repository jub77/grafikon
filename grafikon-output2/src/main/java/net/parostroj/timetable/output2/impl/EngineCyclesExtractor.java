package net.parostroj.timetable.output2.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Extracts information for engine cycles.
 *
 * @author jub
 */
public class EngineCyclesExtractor {

    private List<TrainsCycle> cycles;

    public EngineCyclesExtractor(List<TrainsCycle> cycles) {
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
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(TransformUtil.getEngineCycleDescription(cycle));
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

    private EngineCycleRow createRow(TrainsCycleItem current, TrainsCycleItem previous) {
        EngineCycleRow row = new EngineCycleRow();
        row.setTrainName(current.getTrain().getName());
        row.setFromTime(TimeConverter.convertFromIntToText(current.getStartTime()));
        row.setToTime(TimeConverter.convertFromIntToText(current.getEndTime()));
        row.setFromAbbr(current.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(current.getToInterval().getOwnerAsNode().getAbbr());
        // set wait time - in real seconds (not the model ones)
        if (previous != null) {
            int time = current.getStartTime() - previous.getEndTime();
            // recalculate to real seconds
            Double timeScale = (Double)current.getTrain().getTrainDiagram().getAttribute("time.scale");
            time = (int)Math.round((1.0d / timeScale) * time);
            row.setWait(time);
        }
        return row;
    }
}
