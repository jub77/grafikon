package net.parostroj.timetable.output2.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.output2.impl.TrainUnitCycleCard;

/**
 * Train unit cycle cards.
 *
 * @author jub
 */
@XmlRootElement
public class TrainUnitCycleCards {

    private List<TrainUnitCycleCard> card;

    public TrainUnitCycleCards(List<TrainUnitCycleCard> card) {
        this.card = card;
    }

    public List<TrainUnitCycleCard> getCard() {
        return card;
    }

    public void setCard(List<TrainUnitCycleCard> card) {
        this.card = card;
    }
}
