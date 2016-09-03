package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.WeightTableRow;

/**
 * Storage for weight table row.
 *
 * @author jub
 */
@XmlType(propOrder = {"speed", "weights"})
public class LSWeightTableRow {

    private static final Logger log = LoggerFactory.getLogger(LSWeightTableRow.class);

    private int speed;
    private List<LSWeightLimit> weights;

    public LSWeightTableRow() {
    }

    public LSWeightTableRow(WeightTableRow row) {
        this.speed = row.getSpeed();
        weights = new LinkedList<>();
        for (Map.Entry<LineClass, Integer> entry : row.getWeights().entrySet()) {
            weights.add(new LSWeightLimit(entry.getKey().getId(), entry.getValue()));
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

    public WeightTableRow createWeightTableRow(Function<String, LineClass> lineClassSource, EngineClass engineClass) {
        WeightTableRow row = engineClass.createWeightTableRow(speed);
        if (weights != null) {
            for (LSWeightLimit limit : weights) {
                LineClass lineClass = lineClassSource.apply(limit.getLineClass());
                if (lineClass == null) {
                    log.warn("Non-existent line class: {}", limit.getLineClass());
                } else {
                    row.setWeightInfo(lineClassSource.apply(limit.getLineClass()), limit.getWeight());
                }
            }
        }
        return row;
    }
}
