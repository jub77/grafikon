package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.parostroj.timetable.model.*;

/**
 * Extracts information for train unit cycles.
 *
 * @author jub
 */
public class TrainUnitCyclesExtractor {

    private final List<TrainsCycle> cycles;
    private final Locale locale;
    private final AttributesExtractor ae = new AttributesExtractor();

    public TrainUnitCyclesExtractor(List<TrainsCycle> cycles, Locale locale) {
        this.cycles = cycles;
        this.locale = locale;
    }

    public List<TrainUnitCycle> getTrainUnitCycles() {
        List<TrainUnitCycle> outputCycles = new LinkedList<TrainUnitCycle>();
        for (TrainsCycle cycle : cycles) {
            outputCycles.add(createCycle(cycle));
        }
        return outputCycles;
    }

    private TrainUnitCycle createCycle(TrainsCycle cycle) {
        TrainUnitCycle outputCycle = new TrainUnitCycle();
        outputCycle.setName(cycle.getName());
        outputCycle.setDescription(cycle.getDescription());
        for (TrainsCycleItem item : cycle.getItems()) {
            outputCycle.getRows().add(createRow(item));
        }
        outputCycle.setAttributes(ae.extract(cycle.getAttributes()));
        return outputCycle;
    }

    private TrainUnitCycleRow createRow(TrainsCycleItem item) {
    	TimeConverter c = item.getTrain().getTrainDiagram().getTimeConverter();
        TrainUnitCycleRow row = new TrainUnitCycleRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(c.convertIntToXml(item.getStartTime()));
        row.setToTime(c.convertIntToXml(item.getEndTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        String comment = (item.getComment() == null || item.getComment().trim().equals("")) ? null : item.getComment();
        if (comment != null) {
            comment = item.getTrain().getTrainDiagram().getLocalization().translate(comment, locale);
        }
        row.setComment(comment);
        this.getCustomCyclesItem(row.getCycle(), item);
        return row;
    }

    private void getCustomCyclesItem(List<TrainUnitCustomCycle> list, TrainsCycleItem tuItem) {
        Train train = tuItem.getTrain();
        int startIndex = train.getTimeIntervalList().indexOf(tuItem.getFromInterval());
        int endIndex = train.getTimeIntervalList().indexOf(tuItem.getToInterval());
        for (TrainsCycleType type : train.getTrainDiagram().getCycleTypes()) {
            if (!TrainsCycleType.isDefaultType(type.getName())) {
                List<TrainsCycleItem> items = train.getCycles(type);
                for (TrainsCycleItem item : items) {
                    if (item.getFrom() == tuItem.getFrom() && item.getTo() == tuItem.getTo()) {
                        // the cover the same interval
                        list.add(new TrainUnitCustomCycle(item.getCycle().getType().getName(),
                                item.getCycle().getName(), null, null));
                    } else {
                        int i1 = train.getTimeIntervalList().indexOf(item.getFromInterval());
                        int i2 = train.getTimeIntervalList().indexOf(item.getToInterval());
                        if (startIndex <= i1 && i2 <= endIndex) {
                            list.add(new TrainUnitCustomCycle(item.getCycle().getType().getName(),
                                    item.getCycle().getName(), item.getFromInterval().getOwnerAsNode().getAbbr(),
                                    item.getToInterval().getOwnerAsNode().getAbbr()));
                        }
                    }
                }
            }
        }
    }
}
