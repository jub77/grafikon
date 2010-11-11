package net.parostroj.timetable.output2.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Weight data extractor.
 *
 * @author jub
 */
public class WeightDataExtractor {

    private Train train;
    private List<WeightDataRow> data;

    public WeightDataExtractor(Train train) {
        this.train = train;
        this.data = new LinkedList<WeightDataRow>();
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
            data.add(this.createRow(train, name, item.getFromInterval().getOwnerAsNode(), item.getToInterval().getOwnerAsNode(), weightStr));
        }
        if (data.isEmpty() && weightStr != null) {
            data.add(this.createRow(train, null, null, null, weightStr));
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
                        if (weight != null && item.first.isStop() && item.first.getOwnerAsNode().getType().isStationOrStop())
                            process = true;
                    }
                    if (process) {
                        // add data
                        data.add(this.createRow(cycle, startNode, item.first.getOwnerAsNode(), weight));
                        // set new start node
                        startNode = item.first.getOwnerAsNode();
                    }
                }
            }
        }
    }

    private void collapseData() {
        Iterator<WeightDataRow> i = data.listIterator();
        WeightDataRow lastRow = i.next();
        String lastEngine = lastRow.getEngine();
        while (i.hasNext()) {
            WeightDataRow row = i.next();
            if (lastEngine.equals(row.getEngine()) && lastRow.getWeight().equals(row.getWeight())) {
                lastRow.setTo(row.getTo());
                i.remove();
            } else {
                if (lastEngine.equals(row.getEngine()))
                    row.setEngine(null);
                else
                    lastEngine = row.getEngine();
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
        if (train.getCycles(TrainsCycleType.ENGINE_CYCLE).isEmpty()) {
            return false;
        }
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            if (item.getCycle().getAttribute("engine.class") == null) {
                return false;
            }
        }

        return true;
    }

    List<WeightDataRow> getData() {
        return Collections.unmodifiableList(data);
    }

    private WeightDataRow createRow(TrainsCycle cycle, Node from, Node to, Integer weight) {
        String engineStr = cycle != null ? TransformUtil.getEngineCycleDescription(cycle) : null;
        String fromStr = from.getName();
        String toStr = to.getName();
        String weightStr = weight.toString();
        return new WeightDataRow(engineStr, fromStr, toStr, weightStr);
    }

    private WeightDataRow createRow(Train train, String engine, Node from, Node to, String weight) {
        String fromStr = null;
        String toStr = null;
        if ((train.getStartNode() == from && train.getEndNode() == to) || from == null || to == null) {
            fromStr = null;
            toStr = null;
        } else {
            fromStr = from.getName();
            toStr = to.getName();
        }
        return new WeightDataRow(engine, fromStr, toStr, weight);
    }
}
