package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * Extracts information for driver cycles.
 *
 * @author jub
 */
public class DriverCyclesExtractor {

    private List<TrainsCycle> cycles;
    private TrainDiagram diagram;

    public DriverCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles) {
        this.cycles = cycles;
        this.diagram = diagram;
    }

    public DriverCycles getDriverCycles() {
        List<DriverCycle> outputCyclesList = new LinkedList<DriverCycle>();
        for (TrainsCycle cycle : cycles) {
            outputCyclesList.add(createCycle(cycle));
        }
        DriverCycles outputCycles = new DriverCycles(outputCyclesList);
        // fill in other data
        outputCycles.setRouteNumbers((String)diagram.getAttribute("route.numbers"));
        outputCycles.setRouteStations((String)diagram.getAttribute("route.nodes"));
        outputCycles.setValidity((String)diagram.getAttribute("route.validity"));
        return outputCycles;
    }

    private DriverCycle createCycle(TrainsCycle cycle) {
        DriverCycle outputCycle = new DriverCycle();
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        for (TrainsCycleItem item : cycle.getItems()) {
            outputCycle.getRows().add(this.createRow(item));
        }
        return outputCycle;
    }

    private DriverCycleRow createRow(TrainsCycleItem item) {
        DriverCycleRow row = new DriverCycleRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(TimeConverter.convertFromIntToText(item.getStartTime()));
        row.setToTime(TimeConverter.convertFromIntToText(item.getEndTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        row.setFrom(item.getFromInterval().getOwnerAsNode().getName());
        row.setTo(item.getToInterval().getOwnerAsNode().getName());
        return row;
    }
}
