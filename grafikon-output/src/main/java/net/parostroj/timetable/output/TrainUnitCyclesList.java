/*
 * TrainUnitCyclesList.java
 * 
 * Created on 11.9.2007, 19:33:20
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Formatter;
import java.util.List;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * List of timetables for nodes.
 * 
 * @author jub
 */
public class TrainUnitCyclesList {
    
    private List<TrainsCycle> trainUnitCycles;
    
    private TrainUnitCyclesListTemplates templates;

    public TrainUnitCyclesList(List<TrainsCycle> trainUnitCycles) {
        TrainsCycleSort sort = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        this.trainUnitCycles = sort.sort(trainUnitCycles);
        templates = new TrainUnitCyclesListTemplates();
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        writer.write(String.format(templates.getHtmlHeader(),templates.getString("train.unit.cycles")));
        int i = 0;
        for (TrainsCycle cycle : trainUnitCycles) {
            this.writeTrainUnitCycle(f, writer, cycle);
            if ((i & 1) == 1)
                writer.write("<div style=\"page-break-before: always;\">&nbsp;</div>");
            i++;
        }
        writer.write(templates.getHtmlFooter());
    }
    
    private void writeTrainUnitCycle(Formatter f, Writer writer, TrainsCycle cycle) throws IOException {
        f.format(templates.getTucHeader(), cycle.getDescription(), cycle.getName(),templates.getString("company"),
                templates.getString("train.unit.cycle"),templates.getString("train.unit.composition"),
                templates.getString("column.train"),templates.getString("column.departure"),
                templates.getString("column.from.to"),templates.getString("column.note"));
        
        for (TrainsCycleItem item : cycle) {
            Train t = item.getTrain();
            String comment = item.getComment();
            if (comment != null && "".equals(comment.trim()))
                comment = null;
            f.format(templates.getTucLine(), t.getName(),TimeConverter.convertFromIntToText(item.getStartTime()),item.getFromInterval().getOwnerAsNode().getAbbr(),item.getToInterval().getOwnerAsNode().getAbbr(), (comment != null) ? comment : "&nbsp;");
        }
        
        writer.write(templates.getTucFooter());
    }    
}
