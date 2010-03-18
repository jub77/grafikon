/*
 * EngineCyclesList.java
 * 
 * Created on 11.9.2007, 19:33:20
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.*;

/**
 * List of timetables for nodes.
 * 
 * @author jub
 */
public class EngineCyclesList {
    
    private List<TrainsCycle> engineCycles;
    
    private EngineCyclesListTemplates templates;

    public EngineCyclesList(List<TrainsCycle> engineCycles) {
        TrainsCycleSort sort = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        this.engineCycles = sort.sort(engineCycles);
        templates = new EngineCyclesListTemplates();
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        writer.write(String.format(templates.getHtmlHeader(),templates.getString("engine.cycles")));
        Iterator<TrainsCycle> iterator = engineCycles.iterator();
        int end = ((engineCycles.size() + 2) / 3) * 3;
        TrainsCycle empty = new TrainsCycle(null, "&nbsp;","&nbsp;", TrainsCycleType.ENGINE_CYCLE);
        for (int i = 0; i < end; i++) {
            if ((i % 3) == 0) {
                writer.write(templates.getRowHeader());
            } else {
                writer.write(templates.getRowMiddle());
            }

            if (iterator.hasNext())
                this.writeEngineCycle(f, writer, iterator.next());
            else
                this.writeEngineCycle(f, writer, empty);
            
            if ((i % 3) == 2) {
                writer.write(templates.getRowFooter());
            }
            if ((i % 9) == 8)
                writer.write("<div style=\"page-break-before: always; font-size: 1mm;\">&nbsp;</div>\n");
        }
        writer.write(templates.getHtmlFooter());
    }
    
    private void writeEngineCycle(Formatter f, Writer writer, TrainsCycle cycle) throws IOException {
        f.format(templates.getEcHeader(), cycle.getName(),TransformUtil.getEngineCycleDescription(cycle),templates.getString("engine.cycle"),
                templates.getString("column.train"),templates.getString("column.departure"),templates.getString("column.from.to"));
        
        int lastTime = -1;
        for (TrainsCycleItem item : cycle) {
            Train t = item.getTrain();
            if (lastTime != -1) {
                if ((t.getStartTime() - lastTime) > 7200) {
                    writer.write(templates.getEcLineSep());
                }
            }
            f.format(templates.getEcLine(), t.getName(),TimeConverter.convertFromIntToText(item.getStartTime()),item.getFromInterval().getOwnerAsNode().getAbbr(),item.getToInterval().getOwnerAsNode().getAbbr());
            lastTime = t.getEndTime();
        }
        
        writer.write(templates.getEcFooter());
    }    
}
