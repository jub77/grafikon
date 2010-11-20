package net.parostroj.timetable.output2.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainsHelper;
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
    private boolean showWeight;

    public WeightDataExtractor(Train train) {
        this.train = train;
        this.data = new LinkedList<WeightDataRow>();
        this.showWeight = train.getType().getCategory().getKey().equals("freight")
                || !train.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE).isEmpty();
        this.processData();
    }

    private void processData() {
        Integer weight = TrainsHelper.getWeightFromAttribute(train);
        if (!this.checkLineClasses() || !this.checkEngineClasses() || weight != null || !showWeight) {
            this.processOld(weight);
            this.collapseData();
        } else {
            this.processNew();
            this.collapseData();
        }
    }

    private void processOld(Integer weight) {
        List<String> eClasses = null;
        Node startNode = null;
        for (int i = 0; i < train.getTimeIntervalList().size(); i++) {
            TimeInterval interval = train.getTimeIntervalList().get(i);
            if (interval.isLineOwner()) {
                if (eClasses == null)
                    eClasses = this.convertItemList(TrainsHelper.getEngineCyclesForInterval(interval));
            } else {
                if (startNode == null)
                    startNode = interval.getOwnerAsNode();
                else {
                    boolean process = false;
                    List<String> eClasses2 = null;
                    if (interval.isLast())
                        process = true;
                    else {
                        TimeInterval nextInterval = train.getTimeIntervalList().get(i + 1);
                        eClasses2 = this.convertItemList(TrainsHelper.getEngineCyclesForInterval(nextInterval));
                        if (!this.compareEngineLists(eClasses, eClasses2))
                            process = true;
                        if (interval.isStop() && interval.getOwnerAsNode().getType().isStationOrStop())
                            process = true;
                    }
                    if (process) {
                        // add data
                        data.add(this.createRow(eClasses, startNode, interval.getOwnerAsNode(), showWeight ? weight : null));
                        // set new start node
                        startNode = interval.getOwnerAsNode();
                    }
                    eClasses = eClasses2;
                }
            }
        }
    }

    private void processNew() {
        List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> list = TrainsHelper.getWeightList(train);

        if (list == null)
            return;

        Integer weight = null;
        Node startNode = null;
        List<String> eClasses = null;
        for (int i = 0; i < list.size(); i++) {
            Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> item = list.get(i);

            if (item.first.isLineOwner()) {
                // process line interval
                if (weight == null || weight > item.second.first)
                    weight = item.second.first;
                if (eClasses == null)
                    eClasses = this.convertItemList(item.second.second);
            } else {
                // process node interval
                if (startNode == null)
                    startNode = item.first.getOwnerAsNode();
                else {
                    List<String> eClasses2 = null;
                    boolean process = false;
                    if (item.first.isLast())
                        process = true;
                    else {
                        Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> itemNext = list.get(i + 1);
                        eClasses2 = this.convertItemList(itemNext.second.second);
                        if (!this.compareEngineLists(eClasses, eClasses2))
                            process = true;
                        if (weight != null && item.first.isStop() && item.first.getOwnerAsNode().getType().isStationOrStop())
                            process = true;
                    }
                    if (process) {
                        // add data
                        data.add(this.createRow(eClasses, startNode, item.first.getOwnerAsNode(), weight));
                        // set new start node
                        startNode = item.first.getOwnerAsNode();
                    }
                    eClasses = eClasses2;
                }
            }
        }
    }

    private List<String> convertItemList(List<TrainsCycleItem> items) {
        List<String> result = new LinkedList<String>();
        for (TrainsCycleItem item : items) {
            result.add(TransformUtil.getEngineCycleDescription(item.getCycle()));
        }
        return result;
    }

    private boolean compareEngineLists(List<String> list1, List<String> list2) {
        List<String> test = new LinkedList<String>(list1);
        for (String eClass : list2) {
            boolean removed = test.remove(eClass);
            if (!removed)
                return false;
        }
        return test.isEmpty();
    }

    private void collapseData() {
        if (data.isEmpty())
            return;
        Iterator<WeightDataRow> i = data.listIterator();
        WeightDataRow lastRow = i.next();
        while (i.hasNext()) {
            WeightDataRow row = i.next();
            if (lastRow.getEngines().containsAll(row.getEngines()) && ((lastRow.getWeight() != null && lastRow.getWeight().equals(row.getWeight())) || lastRow.getWeight() == row.getWeight())) {
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
            if (lastRow.isRowEmpty())
                data.clear();
        }
    }

    private boolean checkLineClasses() {
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (interval.isLineOwner()) {
                if (interval.getLineClass() == null)
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

    private WeightDataRow createRow(List<String> engineClasses, Node from, Node to, Integer weight) {
        String fromStr = from.getName();
        String toStr = to.getName();
        return new WeightDataRow(engineClasses, fromStr, toStr, weight);
    }
}
