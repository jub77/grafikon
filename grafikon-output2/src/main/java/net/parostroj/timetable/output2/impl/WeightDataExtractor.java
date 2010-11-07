package net.parostroj.timetable.output2.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
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
        String weightStr = (String) train.getAttribute("weight.info");
        if (weightStr != null && weightStr.trim().equals(""))
            weightStr = null;
        if (!this.checkLineClasses() || !this.checkEngineClasses() || weightStr != null) {
            this.processOld(weightStr);
        } else {
            this.processNew();
            this.collapseData();
        }
    }

    private void processOld(String weightStr) {
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            String name = TransformUtil.getEngineDescription(item.getCycle());
            if (name != null || weightStr != null)
                data.add(this.createRow(train, name, item.getFromInterval().getOwnerAsNode(), item.getToInterval().getOwnerAsNode(), weightStr));
        }
        if (data.isEmpty() && weightStr != null) {
            data.add(this.createRow(train, null, null, null, weightStr));
        }
    }

    private void processNew() {
        List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> list = TrainsHelper.getWeightList(train);

        if (list == null)
            return;

        Integer weight = null;
        Node startNode = null;
        List<EngineClass> eClasses = null;
        for (int i = 0; i < list.size(); i++) {
            Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> item = list.get(i);

            if (item.first.isLineOwner()) {
                // process line interval
                if (weight == null || weight > item.second.first)
                    weight = item.second.first;
                eClasses = TrainsHelper.getEngineClasses(item.second.second);
            } else {
                // process node interval
                if (startNode == null)
                    startNode = item.first.getOwnerAsNode();
                else {
                    boolean process = false;
                    if (item.first.isLast())
                        process = true;
                    else {
                        Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> itemNext = list.get(i + 1);
                        List<EngineClass> eClasses2 = TrainsHelper.getEngineClasses(itemNext.second.second);
                        if (!this.compareEngineLists(eClasses, eClasses2))
                            process = true;
                        if (weight != null && item.first.isStop() && item.first.getOwnerAsNode().getType().isStation())
                            process = true;
                    }
                    if (process) {
                        // add data
                        data.add(this.createRow(eClasses, startNode, item.first.getOwnerAsNode(), weight));
                        // set new start node
                        startNode = item.first.getOwnerAsNode();
                    }
                }
            }
        }
    }

    private boolean compareEngineLists(List<EngineClass> list1, List<EngineClass> list2) {
        List<EngineClass> test = new LinkedList<EngineClass>(list1);
        for (EngineClass eClass : list2) {
            boolean removed = test.remove(eClass);
            if (!removed)
                return false;
        }
        return test.isEmpty();
    }

    private void collapseData() {
        Iterator<WeightDataRow> i = data.listIterator();
        WeightDataRow lastRow = i.next();
        while (i.hasNext()) {
            WeightDataRow row = i.next();
            if (lastRow.getEngines().containsAll(row.getEngines()) && lastRow.getWeight().equals(row.getWeight())) {
                lastRow.setTo(row.getTo());
                i.remove();
            } else {
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

    private WeightDataRow createRow(List<EngineClass> engineClasses, Node from, Node to, Integer weight) {
        List<String> ecs = new LinkedList<String>();
        for (EngineClass c : engineClasses) {
            ecs.add(c.getName());
        }
        String fromStr = from.getName();
        String toStr = to.getName();
        String weightStr = weight.toString();
        return new WeightDataRow(ecs, fromStr, toStr, weightStr);
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
        return new WeightDataRow(engine == null ? Collections.<String>emptyList() : Collections.singletonList(engine), fromStr, toStr, weight);
    }
}
