package net.parostroj.timetable.model.ls.impl3;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.WeightTableRow;

/**
 * Storage for weight table row.
 *
 * @author jub
 */
@XmlType(propOrder = {"speed", "weights"})
public class LSWeightTableRow {

    private int speed;
    private List<LSWeightLimit> weights;

    public LSWeightTableRow() {
    }

    public LSWeightTableRow(WeightTableRow row) {
        this.speed = row.getSpeed();
        weights = new LinkedList<LSWeightLimit>();
        for (Map.Entry<LineClass, Integer> entry : row.getWeights().entrySet()) {
            weights.add(new LSWeightLimit(entry.getKey(), entry.getValue()));
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<LSWeightLimit> getWeights() {
        return weights;
    }

    @XmlElement(name = "weight_limit")
    public void setWeights(List<LSWeightLimit> weights) {
        this.weights = weights;
    }

    public WeightTableRow createWeightTableRow(Net net, EngineClass engineClass) {
        WeightTableRow row = engineClass.createWeightTableRow(speed);
        if (weights != null)
            for (LSWeightLimit limit : weights) {
                row.setWeightInfo(net.getLineClasses().getById(limit.getLineClass()), limit.getWeight());
            }
        return row;
    }
}
