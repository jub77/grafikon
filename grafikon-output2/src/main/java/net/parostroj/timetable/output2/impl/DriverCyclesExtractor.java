package net.parostroj.timetable.output2.impl;

import java.util.*;

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

    private final List<TrainsCycle> cycles;
    private final TrainDiagram diagram;
    private final Locale locale;
    private final AttributesExtractor ae = new AttributesExtractor();

    private RoutesExtractor routesExtractor;

    public DriverCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles, boolean addRoutes, Locale locale) {
        this.cycles = cycles;
        this.diagram = diagram;
        this.locale = locale;
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
        outputCycles.setRouteNumbers(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NUMBERS, String.class));
        outputCycles.setRouteStations(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NODES, String.class));
        outputCycles.setValidity(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_VALIDITY, String.class));
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
    	TimeConverter c = item.getTrain().getDiagram().getTimeConverter();
        DriverCycleRow row = new DriverCycleRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(c.convertIntToXml(item.getStartTime()));
        row.setToTime(c.convertIntToXml(item.getEndTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        row.setFrom(item.getFromInterval().getOwnerAsNode().getName());
        row.setTo(item.getToInterval().getOwnerAsNode().getName());
        String comment = (item.getComment() != null && !item.getComment().trim().equals("")) ? item.getComment() : null;
        if (comment != null) {
            comment = diagram.getLocalization().translate(comment, locale);
        }
        row.setComment(comment);
        return row;
    }
}
