package net.parostroj.timetable.output2.impl;

import java.util.*;

import net.parostroj.timetable.model.*;

/**
 * Extracts information for driver cycles.
 *
 * @author jub
 */
public class DriverCyclesExtractor {

    private final List<TrainsCycle> cycles;
    private final TrainDiagram diagram;
    private final AttributesExtractor ae = new AttributesExtractor();

    private RoutesExtractor routesExtractor;

    public DriverCyclesExtractor(TrainDiagram diagram, List<TrainsCycle> cycles, boolean addRoutes, Locale locale) {
        this.cycles = cycles;
        this.diagram = diagram;
        if (addRoutes) {
            this.routesExtractor = new RoutesExtractor(diagram);
        }
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
        if (this.routesExtractor != null) {
            this.addNetPartRouteInfos(outputCycle, cycle);
        }
        Company company = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        if (company != null) {
            outputCycle.setCompany(CompanyInfo.convert(company));
        }
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
        row.setComment(item.getComment());
        row.setSetupTime(item.getSetupTime());
        // technological time (if the start equals to item start)
        if (item.getFrom() == null && item.getTrain().getTimeIntervalBefore() != null) {
            row.setTechnologicalTime(item.getTrain().getTimeBefore());
        } else if (item.getFrom() != null) {
            row.setTechnologicalTime(item.getFrom().getLength());
        }
        return row;
    }
}
