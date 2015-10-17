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
public class TrainsCycleType implements AttributesHolder, ObjectWithId, Visitable, TrainsCycleTypeAttributes, Observable {

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
    private final Attributes attributes;
    private final ItemWithIdSet<TrainsCycle> cycles;

    private final ListenerSupport listenerSupport;

    public TrainsCycleType(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.cycles = new ItemWithIdSetImpl<TrainsCycle>(
                (type, item) -> {
                    if (type == Event.Type.REMOVED) {
                        item.clear();
                    }
                    diagram.fireCollectionEventObservable(type, item, null, null);
                });
        listenerSupport = new ListenerSupport();
        attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(TrainsCycleType.this, change)));
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
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!ObjectsUtil.compareWithNull(description, this.description)) {
            String oldDescription = this.description;
            this.description = description;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_DESCRIPTION,
                    oldDescription, description)));
        }
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public ItemWithIdSet<TrainsCycle> getCycles() {
        return cycles;
    }

    public String getDescriptionText() {
        return getDescriptionText(Locale.getDefault());
    }

    public String getDescriptionText(Locale locale) {
        String text = null;
        if (isDefaultType(name)) {
            ResourceBundle bundle = ResourceBundleUtil.getBundle(
                    "net.parostroj.timetable.model.cycle_type_texts",
                    TrainsCycleType.class.getClassLoader(), locale, Locale.ENGLISH);
            text = bundle.getString(name);
        } else {
            text = (description != null) ? description : name;
            text = diagram.getLocalization().translate(text, locale);
        }
        return text;
    }

    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeListener(Listener listener) {
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
