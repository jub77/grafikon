package net.parostroj.timetable.output2.impl;

import java.util.List;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;

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
        return null;
    }
}
