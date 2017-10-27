package net.parostroj.timetable.output2.impl;

import java.util.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.parostroj.timetable.model.*;

/**
 * Extracts information for custom cycles.
 *
 * @author jub
 */
public class CustomCyclesExtractor {

    private final List<TrainsCycle> cycles;
    private final AttributesExtractor attributesExtractor = new AttributesExtractor();

    private int counter;

    public CustomCyclesExtractor(List<TrainsCycle> cycles) {
        this.cycles = cycles;
    }

    public List<CustomCycle> getCycles() {
        counter = 0;
        List<CustomCycle> outputCycles = new LinkedList<>();
        BiMap<TrainsCycle, CustomCycle> map = HashBiMap.create();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(this.getCycle(cycle, map));
        }
        // process sequence
        for (CustomCycle outputCycle : outputCycles) {
            TrainsCycle cycle = map.inverse().get(outputCycle);
            if (cycle.isPartOfSequence()) {
                outputCycle.setNext(this.getCycle(cycle.getNext(), map));
            }
        }
        return outputCycles;
    }

    private String getNextId() {
        return Integer.toString(counter++);
    }

    private CustomCycle getCycle(TrainsCycle cycle, BiMap<TrainsCycle, CustomCycle> map) {
        CustomCycle customCycle = map.get(cycle);
        if (customCycle == null) {
            customCycle = this.createCycle(cycle, map);
        }
        return customCycle;
    }

    private CustomCycle createCycle(TrainsCycle cycle, BiMap<TrainsCycle, CustomCycle> map) {
        CustomCycle outputCycle = new CustomCycle();
        outputCycle.setRef(cycle);
        outputCycle.setId(this.getNextId());
        map.put(cycle, outputCycle);
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        outputCycle.setTypeKey(cycle.getType().getKey());
        outputCycle.setTypeName(cycle.getType().getName());
        outputCycle.setAttributes(attributesExtractor.extract(cycle.getAttributes()));
        Iterator<TrainsCycleItem> i = cycle.getItems().iterator();
        TrainsCycleItem current = null;
        TrainsCycleItem previous = null;
        while (i.hasNext()) {
            current = i.next();
            outputCycle.getRows().add(createRow(current, previous));
            previous = current;
        }
        Company company = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        if (company != null) {
            outputCycle.setCompany(CompanyInfo.convert(company));
        }
        return outputCycle;
    }

    private CustomCycleRow createRow(TrainsCycleItem current, TrainsCycleItem previous) {
    	TimeConverter c = current.getTrain().getDiagram().getTimeConverter();
        CustomCycleRow row = new CustomCycleRow();
        row.setRef(current);
        row.setTrainName(current.getTrain().getName());
        row.setFromTime(c.convertIntToXml(current.getStartTime()));
        row.setToTime(c.convertIntToXml(current.getEndTime()));
        row.setFromAbbr(current.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(current.getToInterval().getOwnerAsNode().getAbbr());
        // set wait time - in real seconds (not the model ones)
        if (previous != null) {
            int time = current.getStartTime() - previous.getEndTime();
            // recalculate to real seconds
            Double timeScale = current.getTrain().getDiagram().getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
            time = (int)Math.round((1.0d / timeScale) * time);
            row.setWait(time);
        }
        return row;
    }
}
