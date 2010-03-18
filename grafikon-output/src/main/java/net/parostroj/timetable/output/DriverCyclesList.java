/*
 * DriverCyclesList.java
 * 
 * Created on 11.9.2007, 19:33:20
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Formatter;
import java.util.List;
import java.util.ListIterator;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.Tuple;

/**
 * List of cycles for drivers.
 * 
 * @author jub
 */
public class DriverCyclesList {

    private List<TrainsCycle> driverCycles;
    private Attributes infos;
    private DriverCyclesListTemplates templates;

    public DriverCyclesList(List<TrainsCycle> driverCycles, Attributes infos) {
        TrainsCycleSort sort = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        this.driverCycles = sort.sort(driverCycles);
        this.infos = infos;
        templates = new DriverCyclesListTemplates();
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        writer.write(String.format(templates.getHtmlHeader(), templates.getString("driver.cycles")));
        ListIterator<TrainsCycle> i = driverCycles.listIterator();
        // first pages
        while (i.hasNext()) {
            this.writeDriverCycleFirstPage(f, writer, i.next());
        }
        // second pages
        while (i.hasPrevious()) {
            this.writeDriverCycleSecondPage(f, writer, i.previous());
        }
        writer.write(templates.getHtmlFooter());
    }

    private void writeDriverCycleFirstPage(Formatter f, Writer writer, TrainsCycle cycle) throws IOException {
        String number = (String) infos.get("route.numbers");
        number = (number != null) ? number.replace("\n", "<br>") : "";
        String validity = (String) infos.get("route.validity");
        validity = (validity != null) ? validity : "";
        String routes = (String) infos.get("route.nodes");
        routes = (routes != null) ? routes.replace("\n", "<br>") : "";

        f.format(templates.getDcHeader(), cycle.getName(), number, routes, validity, templates.getString("company"),
                templates.getString("company.part"), templates.getString("train.timetable"), templates.getString("validity.from"),
                templates.getString("driver.cycle"), templates.getString("for.line"));
        writer.write(String.format(templates.getDcFooter(), templates.getString("publisher")));
    }

    private void writeDriverCycleSecondPage(Formatter f, Writer writer, TrainsCycle cycle) throws IOException {
        f.format(templates.getDcHeaderTrains(), templates.getString("column.train"), templates.getString("column.departure"),
                templates.getString("column.from.to"), templates.getString("column.note"));

        List<Tuple<TrainsCycleItem>> conflicts = cycle.checkConflicts();

        for (TrainsCycleItem item : cycle) {
            Train t = item.getTrain();
            f.format(templates.getDcLine(), t.getName(), TimeConverter.convertFromIntToText(item.getStartTime()), item.getFromInterval().getOwnerAsNode().getAbbr(), item.getToInterval().getOwnerAsNode().getAbbr(), (item.getComment() != null) ? item.getComment() : "&nbsp;");
            for (Tuple<TrainsCycleItem> tuple : conflicts) {
                if (tuple.first == item &&
                        tuple.first.getToInterval().getOwnerAsNode() != tuple.second.getFromInterval().getOwnerAsNode()) {
                    f.format(templates.getDcLineMove(), tuple.second.getFromInterval().getOwnerAsNode().getName(), templates.getString("move.to.station"));
                }
            }
        }

        writer.write(templates.getDcFooterTrains());
    }
}
