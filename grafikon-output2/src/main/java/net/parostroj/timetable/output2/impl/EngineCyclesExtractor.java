package net.parostroj.timetable.output2.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Extracts information for engine cycles.
 *
 * @author jub
 */
public class EngineCyclesExtractor {

    private final List<TrainsCycle> cycles;
    private final AttributesExtractor attributesExtractor = new AttributesExtractor();

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

    private EngineCycleRow createRow(TrainsCycleItem current, TrainsCycleItem previous) {
    	TimeConverter c = current.getTrain().getTrainDiagram().getTimeConverter();
        EngineCycleRow row = new EngineCycleRow();
        row.setTrainName(current.getTrain().getName());
        row.setFromTime(c.convertIntToXml(current.getStartTime()));
        row.setToTime(c.convertIntToXml(current.getEndTime()));
        row.setFromAbbr(current.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(current.getToInterval().getOwnerAsNode().getAbbr());
        // set wait time - in real seconds (not the model ones)
        if (previous != null) {
            int time = current.getStartTime() - previous.getEndTime();
            // recalculate to real seconds
            Double timeScale = current.getTrain().getTrainDiagram().getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
            time = (int)Math.round((1.0d / timeScale) * time);
            row.setWait(time);
        }
        // check helper status
        if (TrainsHelper.isHelperEngine(current)) {
            row.setHelper(true);
        }
        return row;
    }
}
