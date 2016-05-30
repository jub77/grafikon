/*
 * TrainCycleType.java
 *
 * Created on 15.9.2007, 20:43:01
 */

package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.LocalizedString.Builder;
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

    public static final String ENGINE_CYCLE_KEY = "ENGINE_CYCLE";
    public static final String DRIVER_CYCLE_KEY = "DRIVER_CYCLE";
    public static final String TRAIN_UNIT_CYCLE_KEY = "TRAIN_UNIT_CYCLE";

    public static boolean isDefaultType(String key) {
        return TrainsCycleType.DRIVER_CYCLE_KEY.equals(key) ||
            TrainsCycleType.ENGINE_CYCLE_KEY.equals(key) ||
            TrainsCycleType.TRAIN_UNIT_CYCLE_KEY.equals(key);
    }

    public static boolean isDefaultType(TrainsCycleType type) {
        return isDefaultType(type.getKey());
    }

    private final String id;
    private final TrainDiagram diagram;
    private String key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (!ObjectsUtil.compareWithNull(key, this.key)) {
            String oldKey = this.key;
            this.key = key;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_KEY, oldKey, key)));
        }
    }

    public LocalizedString getName() {
        return attributes.get(ATTR_NAME, LocalizedString.class);
    }

    public void setName(LocalizedString name) {
        attributes.setRemove(ATTR_NAME, name);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public ItemWithIdSet<TrainsCycle> getCycles() {
        return cycles;
    }

    public static LocalizedString getNameForDefaultType(String key) {
        List<Locale> locales = Arrays.asList(Locale.forLanguageTag("cs"), Locale.GERMAN, Locale.forLanguageTag("sk"));
        Builder builder = LocalizedString.newBuilder();
        for (Locale locale : locales) {
            String text = getKeyForLocale(key, locale);
            builder.addStringWithLocale(text, locale);
        }
        builder.setDefaultString(getKeyForLocale(key, Locale.ENGLISH));
        return builder.build();
    }

    private static String getKeyForLocale(String key, Locale locale) {
        ResourceBundle bundle = ResourceBundleUtil.getBundle(
                "net.parostroj.timetable.model.cycle_type_texts",
                TrainsCycleType.class.getClassLoader(), locale, locale);
        String text = bundle.getString(key);
        return text;
    }

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    public boolean isDefaultType() {
        return isDefaultType(this);
    }

    public boolean isEngineType() {
        return ENGINE_CYCLE_KEY.equals(this.getKey());
    }

    public static boolean isEngineType(TrainsCycleType type) {
        return type != null && type.isEngineType();
    }

    public boolean isTrainUnitType() {
        return TRAIN_UNIT_CYCLE_KEY.equals(this.getKey());
    }

    public static boolean isTrainUnitType(TrainsCycleType type) {
        return type != null && type.isTrainUnitType();
    }

    public boolean isDriverType() {
        return DRIVER_CYCLE_KEY.equals(this.getKey());
    }

    public static boolean isDriverType(TrainsCycleType type) {
        return type != null && type.isDriverType();
    }

    @Override
    public String toString() {
        return getName().getDefaultString();
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
        for (TrainsCycle cycle : getCycles()) {
            cycle.accept(visitor);
        }
    }
}
