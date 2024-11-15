package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.Event;

/**
 * Weight information row.
 *
 * @author jub
 */
public class WeightTableRow {

    private final EngineClass engineClass;
    private final int speed;
    private final Map<LineClass, Integer> weights;

    WeightTableRow(EngineClass engineClass, int speed) {
        this.engineClass = engineClass;
        this.speed = speed;
        weights = new HashMap<>();
    }

    public Map<LineClass, Integer> getWeights() {
        return Collections.unmodifiableMap(weights);
    }

    public Integer getWeight(LineClass lineClass) {
        Integer w =  weights.get(lineClass);
        return w == null ? Integer.valueOf(0) : w;
    }

    public void setWeightInfo(LineClass lineClass, Integer weight) {
        Integer previousWeight = weights.put(lineClass, weight);
        if (!Objects.equals(previousWeight, weight)) {
            engineClass.fireEvent(new Event(engineClass, this, new AttributeChange("weight.info", previousWeight, weight, lineClass.getName())));
        }
    }

    public void removeWeightInfo(LineClass lineClass) {
        Integer previousWeight = weights.remove(lineClass);
        if (previousWeight != null) {
            engineClass.fireEvent(new Event(engineClass, this, new AttributeChange("weight.info", previousWeight, null, lineClass.getName())));
        }
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Weight row for speed: " + speed;
    }
}
