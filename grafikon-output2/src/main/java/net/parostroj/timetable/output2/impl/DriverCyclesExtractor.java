package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Extracts information for driver cycles.
 *
 * @author jub
 */
public class DriverCyclesExtractor {

    private List<TrainsCycle> cycles;
    private TrainDiagram diagram;
    private AttributesExtractor ae = new AttributesExtractor();

    private RoutesExtractor routesExtractor;

    public DriverCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles, boolean addRoutes) {
        this.cycles = cycles;
        this.diagram = diagram;
        if (addRoutes)
            this.routesExtractor = new RoutesExtractor(diagram);
    }

    public DriverCycles getDriverCycles() {
        List<DriverCycle> outputCyclesList = new LinkedList<DriverCycle>();
        for (TrainsCycle cycle : cycles) {
            outputCyclesList.add(createCycle(cycle));
        }
        DriverCycles outputCycles = new DriverCycles(outputCyclesList);
        // fill in other data
        outputCycles.setRouteNumbers((String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NUMBERS));
        outputCycles.setRouteStations((String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NODES));
        outputCycles.setValidity((String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_VALIDITY));
        return outputCycles;
    }

    public DriverCycle createCycle(TrainsCycle cycle) {
        DriverCycle outputCycle = new DriverCycle();
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        for (TrainsCycleItem item : cycle.getItems()) {
            outputCycle.getRows().add(this.createRow(item));
        }
        if (this.routesExtractor != null)
            this.addNetPartRouteInfos(outputCycle, cycle);
        outputCycle.setAttributes(ae.extract(cycle.getAttributes()));
        return outputCycle;
    }

    private void addNetPartRouteInfos(DriverCycle cycle, TrainsCycle tCycle) {
        Set<Line> lines = routesExtractor.getLinesForCycle(tCycle);
        cycle.setRoutes(routesExtractor.getRouteInfosForLines(lines));
    }

    private DriverCycleRow createRow(TrainsCycleItem item) {
    	TimeConverter c = item.getTrain().getTrainDiagram().getTimeConverter();
        DriverCycleRow row = new DriverCycleRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(c.convertIntToText(item.getStartTime()));
        row.setToTime(c.convertIntToText(item.getEndTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        row.setFrom(item.getFromInterval().getOwnerAsNode().getName());
        row.setTo(item.getToInterval().getOwnerAsNode().getName());
        row.setComment((item.getComment() != null && !item.getComment().trim().equals("")) ? item.getComment() : null);
        return row;
    }
}
