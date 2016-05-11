package net.parostroj.timetable.output2.impl;

import java.util.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Extracts information for train unit cycles.
 *
 * @author jub
 */
public class TrainUnitCyclesExtractor {

    private final List<TrainsCycle> cycles;
    private final AttributesExtractor ae = new AttributesExtractor();

    private int counter;

    public TrainUnitCyclesExtractor(List<TrainsCycle> cycles, Locale locale) {
        this.cycles = cycles;
    }

    public List<TrainUnitCycle> getTrainUnitCycles() {
        counter = 0;
        List<TrainUnitCycle> outputCycles = new LinkedList<TrainUnitCycle>();
        BiMap<TrainsCycle, TrainUnitCycle> map = HashBiMap.create();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(this.getCycle(cycle, map));
        }
        // process sequence
        for (TrainUnitCycle outputCycle : outputCycles) {
            TrainsCycle cycle = map.inverse().get(outputCycle);
            if (cycle.isPartOfSequence()) {
                outputCycle.setNext(this.getCycle(cycle.getNext(), map));
            }
        }
        return outputCycles;
    }

    private String getNextId() {
        return Integer.toString(counter++);
    }

    private TrainUnitCycle getCycle(TrainsCycle cycle, BiMap<TrainsCycle, TrainUnitCycle> map) {
        TrainUnitCycle trainUnitCycle = map.get(cycle);
        if (trainUnitCycle == null) {
            trainUnitCycle = this.createCycle(cycle, map);
        }
        return trainUnitCycle;
    }

    private TrainUnitCycle createCycle(TrainsCycle cycle, BiMap<TrainsCycle, TrainUnitCycle> map) {
        TrainUnitCycle outputCycle = new TrainUnitCycle();
        outputCycle.setId(this.getNextId());
        map.put(cycle, outputCycle);
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        for (TrainsCycleItem item : cycle.getItems()) {
            outputCycle.getRows().add(createRow(item));
        }
        outputCycle.setAttributes(ae.extract(cycle.getAttributes()));
        Company company = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        if (company != null) {
            outputCycle.setCompany(CompanyInfo.convert(company));
        }
        return outputCycle;
    }

    private TrainUnitCycleRow createRow(TrainsCycleItem item) {
    	TimeConverter c = item.getTrain().getDiagram().getTimeConverter();
        TrainUnitCycleRow row = new TrainUnitCycleRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(c.convertIntToXml(item.getStartTime()));
        row.setToTime(c.convertIntToXml(item.getEndTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        LocalizedString lComment = item.getComment();
        String comment = ObjectsUtil.checkAndTrim(lComment == null ? null : lComment.getDefaultString());
        row.setComment(comment);
        this.getCustomCyclesItem(row.getCycle(), item);
        return row;
    }

    private void getCustomCyclesItem(List<TrainUnitCustomCycle> list, TrainsCycleItem tuItem) {
        Train train = tuItem.getTrain();
        int startIndex = train.getTimeIntervalList().indexOf(tuItem.getFromInterval());
        int endIndex = train.getTimeIntervalList().indexOf(tuItem.getToInterval());
        for (TrainsCycleType type : train.getDiagram().getCycleTypes()) {
            if (!TrainsCycleType.isDefaultType(type)) {
                List<TrainsCycleItem> items = train.getCycles(type);
                for (TrainsCycleItem item : items) {
                    String typeName = item.getCycle().getType().getName();
                    if (item.getFrom() == tuItem.getFrom() && item.getTo() == tuItem.getTo()) {
                        // the cover the same interval
                        list.add(new TrainUnitCustomCycle(typeName,
                                item.getCycle().getName(), null, null));
                    } else {
                        int i1 = train.getTimeIntervalList().indexOf(item.getFromInterval());
                        int i2 = train.getTimeIntervalList().indexOf(item.getToInterval());
                        if (startIndex <= i1 && i2 <= endIndex) {
                            list.add(new TrainUnitCustomCycle(typeName,
                                    item.getCycle().getName(), item.getFromInterval().getOwnerAsNode().getAbbr(),
                                    item.getToInterval().getOwnerAsNode().getAbbr()));
                        }
                    }
                }
            }
        }
    }
}
