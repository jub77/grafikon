package net.parostroj.timetable.output;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.*;

/**
 * Creates information about train allowed weight and engine.
 *
 * @author jub
 */
public class TrainEngineWeightRows {

    private Train train;
    private List<TrainEWDataRow> data;

    public TrainEngineWeightRows(Train train) {
        this.train = train;
        this.data = new LinkedList<TrainEWDataRow>();
        this.processData();
    }

    private void processData() {
        if (!this.checkLineClasses() || !this.checkEngineClasses()) {
            this.processOld();
        } else {
            this.processNew();
            this.collapseData();
        }
    }

    private void processOld() {
        String weightStr = (String) train.getAttribute("weight.info");
        if (weightStr != null && weightStr.trim().equals(""))
            weightStr = null;
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            String name = TransformUtil.getEngineCycleDescription(item.getCycle());
            data.add(new TrainEWDataRow(train, name, item.getFromInterval().getOwnerAsNode(), item.getToInterval().getOwnerAsNode(), weightStr));
        }
        if (data.size() == 0 && weightStr != null) {
            data.add(new TrainEWDataRow(train, null, null, null, weightStr));
        }
    }

    private void processNew() {
        List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> list = TrainsHelper.getWeightList(train);

        if (list == null)
            return;

        Integer weight = null;
        Node startNode = null;
        TrainsCycle cycle = null;
        for (int i = 0; i < list.size(); i++) {
            Pair<TimeInterval, Pair<Integer, TrainsCycleItem>> item = list.get(i);

            if (item.first.isLineOwner()) {
                // process line interval
                if (weight == null || weight > item.second.first)
                    weight = item.second.first;
                cycle = item.second.second.getCycle();
            } else {
                // process node interval
                if (startNode == null)
                    startNode = item.first.getOwnerAsNode();
                else {
                    boolean process = false;
                    if (item.first.isLast())
                        process = true;
                    else {
                        Pair<TimeInterval, Pair<Integer, TrainsCycleItem>> itemNext = list.get(i + 1);
                        if (cycle != null && !cycle.equals(itemNext.second.second.getCycle()))
                            process = true;
                        if (weight != null && item.first.isStop() && item.first.getOwnerAsNode().getType().isStation())
                            process = true;
                    }
                    if (process) {
                        // add data
                        data.add(new TrainEWDataRow(cycle, startNode, item.first.getOwnerAsNode(), weight));
                        // set new start node
                        startNode = item.first.getOwnerAsNode();
                    }
                }
            }
        }
    }

    private void collapseData() {
        Iterator<TrainEWDataRow> i = data.listIterator();
        TrainEWDataRow lastRow = i.next();
        while (i.hasNext()) {
            TrainEWDataRow row = i.next();
            if (lastRow.getEngine().equals(row.getEngine()) && lastRow.getWeight().equals(row.getWeight())) {
                lastRow.setTo(row.getTo());
                i.remove();
            } else {
                if (lastRow.getEngine().equals(row.getEngine()))
                    row.setEngine(null);
                lastRow = row;
            }
        }
        // set from to to null, where there is only one row
        if (data.size() == 1) {
            lastRow.setFrom(null);
            lastRow.setTo(null);
        }
    }

    private boolean checkLineClasses() {
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (interval.isLineOwner()) {
                if (interval.getOwnerAsLine().getAttribute("line.class") == null)
                    return false;
            }
        }
        return true;
    }

    private boolean checkEngineClasses() {
        if (train.getCycles(TrainsCycleType.ENGINE_CYCLE).size() == 0) {
            return false;
        }
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            if (item.getCycle().getAttribute("engine.class") == null) {
                return false;
            }
        }

        return true;
    }

    List<TrainEWDataRow> getData() {
        return Collections.unmodifiableList(data);
    }
}

class TrainEWDataRow {

    private String engine;
    private String from;
    private String to;
    private String weight;

    public TrainEWDataRow(TrainsCycle cycle, Node from, Node to, Integer weight) {
        this.engine = cycle != null ? TransformUtil.getEngineCycleDescription(cycle) : null;
        this.from = from.getName();
        this.to = to.getName();
        this.weight = weight.toString();
    }

    public TrainEWDataRow(Train train, String engine, Node from, Node to, String weight) {
        this.engine = engine;
        this.weight = weight;
        if ((train.getStartNode() == from && train.getEndNode() == to) || from == null || to == null) {
            this.from = null;
            this.to = null;
        } else {
            this.from = from.getName();
            this.to = to.getName();
        }
    }

    public String getEngine() {
        return engine;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getWeight() {
        return weight;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}