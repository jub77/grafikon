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
    private final Locale locale;
    private final TrainDiagram diagram;

    private int counter;

    public CustomCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles, Locale locale) {
        this.diagram = diagram;
        this.cycles = cycles;
        this.locale = locale;
    }

    public List<CustomCycle> getCycles() {
        counter = 0;
        List<CustomCycle> outputCycles = new LinkedList<CustomCycle>();
        BiMap<TrainsCycle, CustomCycle> map = HashBiMap.create();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(this.getCycle(cycle, map));
        }
        // process sequence
        for (CustomCycle outputCycle : outputCycles) {
            TrainsCycle cycle = map.inverse().get(outputCycle);
            if (cycle.isPartOfSequence()) {
                outputCycle.setNextInSequence(new ArrayList<CustomCycle>());
                cycle.applyToSequence(next -> {
                    if (next != cycle) {
                        CustomCycle nextCustomCycle = this.getCycle(next, map);
                        outputCycle.getNextInSequence().add(nextCustomCycle);
                    }
                });
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
        outputCycle.setId(this.getNextId());
        map.put(cycle, outputCycle);
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        outputCycle.setType(diagram.getLocalization().translate(cycle.getType().getName(), locale));
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
    	TimeConverter c = current.getTrain().getDiagram().getTimeConverter();
        CustomCycleRow row = new CustomCycleRow();
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
