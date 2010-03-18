/*
 * EndPositionsList.java
 * 
 * Created on 11.9.2007, 19:33:20
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Formatter;
import java.util.List;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * List of end positions.
 * 
 * @author jub
 */
public class EndPositionsList {
    
    private TrainDiagram diagram;
    
    private EndPositionsTemplates templates;

    public EndPositionsList(TrainDiagram diagram) {
        this.diagram = diagram;
        templates = new EndPositionsTemplates();
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        f.format(templates.getHtmlHeader(), templates.getString("title"), templates.getString("title"));
        
        // engines
        f.format(templates.getEpSection(), templates.getString("section.ec"));
        for (TrainsCycle ecCycle : this.sortTrainsCycleList(diagram.getCycles(TrainsCycleType.ENGINE_CYCLE))) {
            if (!ecCycle.isEmpty()) {
                TrainsCycleItem end = ecCycle.getItems().get(ecCycle.getItems().size() - 1);
                String endName = end.getToInterval().getOwnerAsNode().getName();
                f.format(templates.getEpLine(), ecCycle.getName(), TransformUtil.getEngineCycleDescription(ecCycle), endName, end.getTrain().getName());
            }
        }
        writer.write(templates.getEpSectionFooter());
        
        // train units
        f.format(templates.getEpSection(), templates.getString("section.tuc"));
        for (TrainsCycle tucCycle : this.sortTrainsCycleList(diagram.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE))) {
            if (!tucCycle.isEmpty()) {
                TrainsCycleItem end = tucCycle.getItems().get(tucCycle.getItems().size() - 1);
                String endName = end.getToInterval().getOwnerAsNode().getName();
                f.format(templates.getEpLine(), tucCycle.getName(), tucCycle.getDescription(), endName, end.getTrain().getName());
            }
        }
        writer.write(templates.getEpSectionFooter());
        
        writer.write(templates.getHtmlFooter());
    }

    private List<TrainsCycle> sortTrainsCycleList(List<TrainsCycle> list) {
        TrainsCycleSort sort = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return sort.sort(list);
    }
}
