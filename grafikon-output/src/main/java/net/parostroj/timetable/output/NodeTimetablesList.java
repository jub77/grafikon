/*
 * NodeTimetablesList.java
 * 
 * Created on 11.9.2007, 19:33:20
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import net.parostroj.timetable.actions.NodeFilter;
import net.parostroj.timetable.actions.NodeSort;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.*;

/**
 * List of timetables for nodes.
 * 
 * @author jub
 */
public class NodeTimetablesList {
    
    private List<Node> nodes;
    private NodeTimetablesListTemplates templates;
    private TrainDiagram diagram;

    public NodeTimetablesList(Collection<Node> aNodes, TrainDiagram diagram) {
        NodeSort s = new NodeSort(NodeSort.Type.ASC);
        nodes = s.sort(aNodes, new NodeFilter() {

            @Override
            public boolean check(Node node) {
                return node.getType().isStation() || node.getType().isStop();
            }
        });
        templates = new NodeTimetablesListTemplates();
        this.diagram = diagram;
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        writer.write(String.format(templates.getHtmlHeader(),templates.getString("node.timetable")));
        for (Node node : nodes) {
            f.format(templates.getTimetableHeader(), node.getName(),
                templates.getString("column.train"),
                templates.getString("column.from"),
                templates.getString("column.arrival"),
                templates.getString("column.track"),
                templates.getString("column.departure"),
                templates.getString("column.to"),
                templates.getString("column.notes"),
                templates.getString("column.end")
              );
            for (TimeInterval i : this.collectIntervals(node)) {
                this.writeLine(f, writer, i);
            }
            writer.write(templates.getTimetableFooter());
        }
        writer.write(templates.getHtmlFooter());
    }
    
    private void writeLine(Formatter f, Writer writer, TimeInterval i) throws IOException {
        TimeInterval from = i.getTrain().getIntervalBefore(i);
        TimeInterval to = i.getTrain().getIntervalAfter(i);
        
        String fromNodeName = TransformUtil.getFromAbbr(i);
        if (fromNodeName == null)
            fromNodeName = "&nbsp;";
        String toNodeName = TransformUtil.getToAbbr(i);
        if (toNodeName == null)
            toNodeName = "&nbsp;";
        String endNodeName = "&nbsp;";
        if (i.getOwner() != i.getTrain().getEndNode())
            endNodeName = i.getTrain().getEndNode().getAbbr();
        
        String fromTime = (from == null && !i.isTechnological()) ? "&nbsp;" : TimeConverter.convertFromIntToText(i.getStart());
        String toTime = (to == null && !i.isTechnological()) ? "&nbsp;" : TimeConverter.convertFromIntToText(i.getEnd());
        
        String comment = this.generateComment(i);
        
        f.format(templates.getTimetableLine(), i.getTrain().getName(),fromNodeName,fromTime,i.getTrack().getNumber(),toTime,toNodeName,comment,endNodeName);
    }
    
    private TimeIntervalList collectIntervals(Node node) {
        TimeIntervalList list = new TimeIntervalList();
        for (NodeTrack track : node.getTracks()) {
            for (TimeInterval i : track.getTimeIntervalList()) {
                list.addIntervalByNormalizedStartTime(i);
            }
        }
        return list;
    }
    
    private String generateComment(TimeInterval interval) {
        // technological time handle differently
        if (interval.isTechnological())
            return templates.getString("technological.time");

        StringBuilder comment = new StringBuilder();
        this.generateCommentWithLength(interval, comment);
        this.generateCommentForEngineCycle(interval, comment);
        this.generateCommentForTrainUnitCycle(interval, comment);
        // comment itself
        String commentStr = (String)interval.getAttribute("comment");
        if (commentStr != null && !commentStr.equals("")) {
            this.appendDelimiter(comment);
            comment.append(commentStr);
        }
        if (Boolean.TRUE.equals(interval.getAttribute("occupied"))) {
            this.appendDelimiter(comment);
            comment.append(TrainTimetablesListTemplates.getString("entry.occupied"));
        }
        if (comment.length() == 0)
            return "&nbsp;";
        else
            return comment.toString();
    }
    
    private void generateCommentForEngineCycle(TimeInterval interval, StringBuilder comment) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            if (item.getToInterval() == interval) {
                // end
                TrainsCycleItem itemNext = item.getCycle().getNextItem(item);
                if (itemNext != null) {
                    this.appendDelimiter(comment);
                    comment.append(templates.getString("engine.to"));
                    comment.append(' ').append(itemNext.getTrain().getName());
                    comment.append(" (").append(TimeConverter.convertFromIntToText(itemNext.getStartTime()));
                    comment.append(')');
                }
            }
            if (item.getFromInterval() == interval) {
                // start
                this.appendDelimiter(comment);
                comment.append(templates.getString("engine")).append(": ");
                comment.append(item.getCycle().getName()).append(" (");
                comment.append(TransformUtil.getEngineCycleDescription(item.getCycle())).append(')');
            }
        }
    }
    
    private void generateCommentForTrainUnitCycle(TimeInterval interval, StringBuilder comment) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE)) {
            // end
            if (item.getToInterval() == interval) {
                TrainsCycleItem itemNext = item.getCycle().getNextItem(item);
                if (itemNext != null) {
                    this.appendDelimiter(comment);
                    comment.append(templates.getString("train.unit")).append(": ");
                    comment.append(item.getCycle().getName()).append(" (");
                    comment.append(item.getCycle().getDescription()).append(") ");
                    comment.append(templates.getString("move.to"));
                    comment.append(' ').append(itemNext.getTrain().getName());
                    comment.append(" (").append(TimeConverter.convertFromIntToText(itemNext.getStartTime()));
                    comment.append(')');
                }
            }
            // start
            if (item.getFromInterval() == interval) {
                this.appendDelimiter(comment);
                comment.append(templates.getString("train.unit")).append(": ");
                comment.append(item.getCycle().getName()).append(" (");
                comment.append(item.getCycle().getDescription()).append(')');
            }
        }
    }

    private void generateCommentWithLength(TimeInterval interval, StringBuilder comment) {
        Train train = interval.getTrain();
        if (train.getIntervalAfter(interval) != null && interval.isStop() && train.getType().getCategory().getKey().equals("freight")) {
            Pair<Node, Integer> length = TrainsHelper.getNextLength(interval.getOwnerAsNode(), train, diagram);
            if (length == null) {
                // check old style comment
                Integer weight = TrainsHelper.getWeightFromAttribute(train);
                if (weight != null)
                    length = new Pair<Node, Integer>(train.getEndNode(), TrainsHelper.convertWeightToLength(train, diagram, weight));
            }
            // if length was calculated
            if (length != null) {
                // update length with station lengths
                length.second = TrainsHelper.updateNextLengthWithStationLengths(interval.getOwnerAsNode(), train, length.second);
                comment.append("[");
                comment.append(length.second);
                LengthUnit lengthUnitObj = (LengthUnit) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT);
                String lengthUnit = lengthUnitObj == null ? "" : lengthUnitObj.getUnitsOfString();
                comment.append(lengthUnit);
                comment.append(" (").append(length.first.getAbbr()).append(")");
                comment.append("]");
            }
        }
    }

    private void appendDelimiter(StringBuilder str) {
        if (str.length() > 0)
            str.append(", ");
    }
}
