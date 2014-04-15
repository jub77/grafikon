package net.parostroj.timetable.output2.impl;

import java.util.*;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TransformUtil;
import net.parostroj.timetable.utils.Triplet;

/**
 * Weight data extractor.
 *
 * @author jub
 */
public class WeightDataExtractor {

    private final Train train;
    private final List<WeightDataRow> data;

    public WeightDataExtractor(Train train) {
        this.train = train;
        this.data = new LinkedList<WeightDataRow>();

        this.processData();
        this.collapseData();
    }

    private void testWeights(List<Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>> list) {
        boolean showWeight = (train.getType() != null && train.getType().getAttributes().getBool(TrainType.ATTR_SHOW_WEIGHT_INFO))
            || !train.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE).isEmpty();
        // test if all weight are defined
        boolean defined = true;
        for (Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>> item : list) {
            if (item.first.isLineOwner() && item.second == null) {
                defined = false;
                break;
            }
        }
        // clear if all weights are not defined or shouldn't be shown
        if (!defined || !showWeight)
            for (Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>> item : list) {
                if (item.first.isLineOwner())
                    item.second = null;
            }
    }

    private void processData() {
        List<Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>> list = TrainsHelper.getWeightList(train);
        this.testWeights(list);

        Integer weight = null;
        Node startNode = null;
        List<String> eClasses = null;
        for (int i = 0; i < list.size(); i++) {
            Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>> item = list.get(i);

            if (item.first.isLineOwner()) {
                // process line interval
                if (weight == null || (item.second != null && weight > item.second))
                    weight = item.second;
                if (eClasses == null)
                    eClasses = this.convertItemList(item.third);
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
                        Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>> itemNext = list.get(i + 1);
                        eClasses2 = this.convertItemList(itemNext.third);
                        if (!this.compareEngineLists(eClasses, eClasses2))
                            process = true;
                        else if (weight != null && item.first.isStop() && item.first.getOwnerAsNode().getType().isStation())
                            process = true;
                    }
                    if (process) {
                        // add data
                        data.add(this.createRow(eClasses, startNode, item.first.getOwnerAsNode(), weight));
                        // set new start node and reset weight
                        startNode = item.first.getOwnerAsNode();
                        weight = null;
                    }
                    eClasses = eClasses2;
                }
            }
        }
    }

    private List<String> convertItemList(Collection<TrainsCycleItem> items) {
        List<String> result = new LinkedList<String>();
        for (TrainsCycleItem item : items) {
            result.add(TransformUtil.getEngineCycleDescription(item.getCycle()));
        }
        return result;
    }

    private boolean compareEngineLists(List<String> list1, List<String> list2) {
        if (list1 == null || list2 == null)
            return list1 == list2;
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
            if (this.compareEngineLists(lastRow.getEngines(), row.getEngines()) && ((lastRow.getWeight() != null && lastRow.getWeight().equals(row.getWeight())) || lastRow.getWeight() == row.getWeight())) {
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

    public List<WeightDataRow> getData() {
        return Collections.unmodifiableList(data);
    }

    private WeightDataRow createRow(List<String> engineClasses, Node from, Node to, Integer weight) {
        String fromStr = from.getName();
        String toStr = to.getName();
        return new WeightDataRow(engineClasses, fromStr, toStr, weight);
    }
}
