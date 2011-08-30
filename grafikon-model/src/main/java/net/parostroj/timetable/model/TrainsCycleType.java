/*
 * TrainCycleType.java
 * 
 * Created on 15.9.2007, 20:43:01
 */

package net.parostroj.timetable.model;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Train cycle type.
 * 
 * @author jub
 */
public class TrainsCycleType implements AttributesHolder, ObjectWithId, Visitable {

    public static final String ENGINE_CYCLE = "ENGINE_CYCLE";
    public static final String DRIVER_CYCLE = "DRIVER_CYCLE";
    public static final String TRAIN_UNIT_CYCLE = "TRAIN_UNIT_CYCLE";

    public static boolean isDefaultType(String type) {
        return TrainsCycleType.DRIVER_CYCLE.equals(type) ||
            TrainsCycleType.ENGINE_CYCLE.equals(type) ||
            TrainsCycleType.TRAIN_UNIT_CYCLE.equals(type);
    }

    private final String id;
    private String name;
    private Attributes attributes;
    private List<TrainsCycle> cycles;

    public TrainsCycleType(String id) {
        this.id = id;
        this.cycles = new LinkedList<TrainsCycle>();
        this.setAttributes(new Attributes());
    }
    
    public TrainsCycleType(String id, String name) {
        this(id);
        this.name = name;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    public List<TrainsCycle> getCycles() {
        return cycles;
    }
    
    public void setCycles(List<TrainsCycle> cycles) {
        this.cycles = cycles;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
        for (TrainsCycle cycle : getCycles()) {
            cycle.accept(visitor);
        }
    }
}
