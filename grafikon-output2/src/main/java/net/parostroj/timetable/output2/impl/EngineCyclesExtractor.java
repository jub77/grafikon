package net.parostroj.timetable.output2.impl;

import java.util.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Extracts information for engine cycles.
 *
 * @author jub
 */
public class EngineCyclesExtractor {

    private final List<TrainsCycle> cycles;
    private final AttributesExtractor attributesExtractor = new AttributesExtractor();

    private int counter;

    public EngineCyclesExtractor(List<TrainsCycle> cycles) {
        this.cycles = cycles;
    }

    public List<EngineCycle> getEngineCycles() {
        counter = 0;
        List<EngineCycle> outputCycles = new LinkedList<EngineCycle>();
        BiMap<TrainsCycle, EngineCycle> map = HashBiMap.create();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(this.getCycle(cycle, map));
        }
        // process sequence
        for (EngineCycle outputCycle : outputCycles) {
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

    private EngineCycle getCycle(TrainsCycle cycle, BiMap<TrainsCycle, EngineCycle> map) {
        EngineCycle engineCycle = map.get(cycle);
        if (engineCycle == null) {
            engineCycle = this.createCycle(cycle, map);
        }
        return engineCycle;
    }

    private EngineCycle createCycle(TrainsCycle cycle, BiMap<TrainsCycle, EngineCycle> map) {
        EngineCycle outputCycle = new EngineCycle();
        outputCycle.setId(this.getNextId());
        map.put(cycle, outputCycle);
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
        Company company = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        if (company != null) {
            outputCycle.setCompany(CompanyInfo.convert(company));
        }
        return outputCycle;
    }

    private EngineCycleRow createRow(TrainsCycleItem current, TrainsCycleItem previous) {
    	TimeConverter c = current.getTrain().getDiagram().getTimeConverter();
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
            Double timeScale = current.getTrain().getDiagram().getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
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
