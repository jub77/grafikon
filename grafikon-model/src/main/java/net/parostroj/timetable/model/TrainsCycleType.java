/*
 * TrainCycleType.java
 *
 * Created on 15.9.2007, 20:43:01
 */

package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceBundleUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Train cycle type.
 *
 * @author jub
 */
public class TrainsCycleType implements AttributesHolder, ObjectWithId, Visitable, TrainsCycleTypeAttributes {

    public static final String ENGINE_CYCLE = "ENGINE_CYCLE";
    public static final String DRIVER_CYCLE = "DRIVER_CYCLE";
    public static final String TRAIN_UNIT_CYCLE = "TRAIN_UNIT_CYCLE";

    public static boolean isDefaultType(String typeName) {
        return TrainsCycleType.DRIVER_CYCLE.equals(typeName) ||
            TrainsCycleType.ENGINE_CYCLE.equals(typeName) ||
            TrainsCycleType.TRAIN_UNIT_CYCLE.equals(typeName);
    }

    public static boolean isDefaultType(TrainsCycleType type) {
        return isDefaultType(type.getName());
    }

    private final String id;
    private final TrainDiagram diagram;
    private String name;
    private String description;
    private final AttributesWrapper attributesWrapper;
    private final List<TrainsCycle> cycles;

    private final GTListenerSupport<TrainsCycleTypeListener, TrainsCycleTypeEvent> listenerSupport;

    private String _cachedDescription;

    public TrainsCycleType(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.cycles = new LinkedList<TrainsCycle>();
        listenerSupport = new GTListenerSupport<TrainsCycleTypeListener, TrainsCycleTypeEvent>(
                (listener, event) -> listener.trainsCycleTypeChanged(event));
        attributesWrapper = new AttributesWrapper(
                (attrs, change) -> listenerSupport.fireEvent(new TrainsCycleTypeEvent(TrainsCycleType.this, change)));
    }

    @Override
    public String getId() {
        return id;
    }

    public TrainDiagram getDiagram() {
        return diagram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this._cachedDescription = null;
            this.listenerSupport.fireEvent(new TrainsCycleTypeEvent(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!ObjectsUtil.compareWithNull(description, this.description)) {
            String oldDescription = this.description;
            this.description = description;
            this._cachedDescription = null;
            this.listenerSupport.fireEvent(new TrainsCycleTypeEvent(this, new AttributeChange(ATTR_DESCRIPTION,
                    oldDescription, description)));
        }
    }

    @Override
    public Attributes getAttributes() {
        return attributesWrapper.getAttributes();
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributesWrapper.setAttributes(attributes);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributesWrapper.getAttributes().get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributesWrapper.getAttributes().set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributesWrapper.getAttributes().remove(key);
    }

    List<TrainsCycle> getCycles() {
        return cycles;
    }

    public String getDescriptionText() {
        return getDescriptionText(Locale.getDefault());
    }

    public String getDescriptionText(Locale locale) {
        if (_cachedDescription == null) {
            if (isDefaultType(name)) {
                ResourceBundle bundle = ResourceBundleUtil.getBundle("net.parostroj.timetable.model.cycle_type_texts", TrainsCycleType.class.getClassLoader(), locale, Locale.ENGLISH);
                _cachedDescription = bundle.getString(name);
            } else {
                _cachedDescription = (description != null) ? description : name;
            }
        }
        return _cachedDescription;
    }

    public void addListener(TrainsCycleTypeListener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeListener(TrainsCycleTypeListener listener) {
        listenerSupport.removeListener(listener);
    }

    public boolean isDefaultType() {
        return isDefaultType(this);
    }

    public boolean isEngineType() {
        return ENGINE_CYCLE.equals(this.getName());
    }

    public boolean isTrainUnitType() {
        return TRAIN_UNIT_CYCLE.equals(this.getName());
    }

    public boolean isDriverType() {
        return DRIVER_CYCLE.equals(this.getName());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
        for (TrainsCycle cycle : getCycles()) {
            cycle.accept(visitor);
        }
    }
}
