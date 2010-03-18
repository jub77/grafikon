package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Weight information row.
 * 
 * @author jub
 */
public class WeightTableRow {

    private int speed;
    private Map<LineClass, Integer> weights;

    public WeightTableRow(int speed) {
        this.speed = speed;
        weights = new HashMap<LineClass, Integer>();
    }
    
    public Map<LineClass, Integer> getWeights() {
        return Collections.unmodifiableMap(weights);
    }
    
    public Integer getWeight(LineClass lineClass) {
        Integer w =  weights.get(lineClass);
        return w == null ? Integer.valueOf(0) : w;
    }
    
    public void setWeightInfo(LineClass lineClass, Integer weight) {
        weights.put(lineClass, weight);
    }
    
    public void removeWeightInfo(LineClass lineClass) {
        weights.remove(lineClass);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Weight row for speed: " + speed;
    }
}
