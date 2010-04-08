package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * Extracts information for train unit cycles cards.
 *
 * @author jub
 */
public class TrainUnitCycleCardsExtractor {

    private TrainDiagram diagram;
    private List<TrainsCycle> cycles;

    public TrainUnitCycleCardsExtractor(TrainDiagram diagram, List<TrainsCycle> cycles) {
        this.diagram = diagram;
        this.cycles = cycles;
    }

    public List<TrainUnitCycleCard> getTrainUnitCycleCards() {
        List<TrainUnitCycleCard> cards = new LinkedList<TrainUnitCycleCard>();
        for (TrainsCycle cycle : cycles) {
            cards.add(createCard(cycle));
        }
        return cards;
    }

    private TrainUnitCycleCard createCard(TrainsCycle cycle) {
        TrainUnitCycleCard card = new TrainUnitCycleCard();
        card.setName(cycle.getName());
        card.setDescription(cycle.getDescription());
        for (TrainsCycleItem item : cycle.getItems()) {
            card.getRows().add(createRow(item));
        }
        return card;
    }

    private TrainUnitCycleCardRow createRow(TrainsCycleItem item) {
        TrainUnitCycleCardRow row = new TrainUnitCycleCardRow();
        row.setTrainName(item.getTrain().getName());
        row.setFromTime(TimeConverter.convertFromIntToText(item.getStartTime()));
        row.setFromAbbr(item.getFromInterval().getOwnerAsNode().getAbbr());
        row.setToAbbr(item.getToInterval().getOwnerAsNode().getAbbr());
        row.setComment((item.getComment() == null || item.getComment().trim().equals("")) ? null : item.getComment());
        return row;
    }
}
