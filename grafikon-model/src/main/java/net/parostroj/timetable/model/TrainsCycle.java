/*
 * TrainsCycle.java
 *
 * Created on 11.9.2007, 20:30:58
 */
package net.parostroj.timetable.model;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.ObservableObject;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.TransformUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Trains cycle.
 *
 * @author jub
 */
public class TrainsCycle implements AttributesHolder, ObjectWithId, Iterable<TrainsCycleItem>, Visitable, TrainDiagramPart, ObservableObject {

    public static final String ATTR_ENGINE_CLASS = "engine.class";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_COMPANY = "company";
    public static final String ATTR_LEVEL = "level";
    public static final String ATTR_FREIGHT = "freight";

    public static final String CATEGORY_USER = "user";

    private final String id;
    private final TrainDiagram diagram;
    private String name;
    private String description;
    private TrainsCycleType type;
    private final Attributes attributes;
    private final List<TrainsCycleItem> items;
    private final ListenerSupport listenerSupport;
    private TrainsCycle next;
    private TrainsCycle previous;

    /**
     * creates instance
     *
     * @param id id
     * @param diagram diagram
     * @param name name of the cycle
     * @param description description
     * @param type type
     */
    public TrainsCycle(String id, TrainDiagram diagram, String name, String description, TrainsCycleType type) {
        this.id = id;
        this.diagram = diagram;
        this.name = name;
        this.description = description;
        this.type = type;
        this.items = new LinkedList<>();
        listenerSupport = new ListenerSupport();
        attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(TrainsCycle.this, change)));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayDescription() {
        if (type == null || !type.isEngineType()) {
            return getDescription();
        } else {
            return TransformUtil.getEngineCycleDescription(this);
        }
    }

    public void setDescription(String description) {
        if (!ObjectsUtil.compareWithNull(description, this.description)) {
            String oldDescription = this.description;
            this.description = description;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_DESCRIPTION,
                    oldDescription, description)));
        }
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

    public TrainsCycle getPrevious() {
        return previous == null ? this : previous;
    }

    public TrainsCycle getNext() {
        return next == null ? this : next;
    }

    public void removeFromSequence() {
        if (isPartOfSequence()) {
            // remember other from sequence
            TrainsCycle other = this.getNext();
            connectTwo(this.getPrevious(), this.getNext());
            this.next = null;
            this.previous = null;
            // fire event
            other.applyToSequence(getSendChangedSequence());
            getSendChangedSequence().accept(this);
        }
    }

    public void connectToSequenceAsNext(TrainsCycle next) {
        if (next.isPartOfSequence()) {
            throw new IllegalArgumentException("Already in sequence: " + next);
        }
        TrainsCycle oldNext = this.getNext();
        connectTwo(this, next);
        connectTwo(next, oldNext);
        // fire event
        this.applyToSequence(getSendChangedSequence());
    }

    public void moveForwardInSequence() {
        TrainsCycle lNext = getNext();
        TrainsCycle afterNext = lNext.getNext();
        if (lNext != this && afterNext != this) {
            // more than 2 circulations
            TrainsCycle lPrevious = getPrevious();
            connectTwo(lPrevious, lNext);
            connectTwo(lNext, this);
            connectTwo(this, afterNext);
        }
        // fire event
        this.applyToSequence(getSendChangedSequence());
    }

    public void moveBackwardInSequence() {
        this.getPrevious().moveForwardInSequence();
    }

    private void connectTwo(TrainsCycle first, TrainsCycle second) {
        if (first == second) {
            first.next = null;
            first.previous = null;
        } else {
            first.next = second;
            second.previous = first;
        }
    }

    public boolean isPartOfSequence() {
        // connection is circular, so only one direction test is needed
        return getPrevious() != this;
    }

    public void applyToSequence(Consumer<TrainsCycle> action) {
        TrainsCycle current = this;
        do {
            action.accept(current);
            current = current.getNext();
        } while (current != this);
    }

    public <T> T aggregateSequence(final T value, BiFunction<T, TrainsCycle, T> function) {
        TrainsCycle current = this;
        T result = value;
        do {
            result = function.apply(result, current);
            current = current.getNext();
        } while (current != this);
        return result;
    }

    private static Consumer<TrainsCycle> getSendChangedSequence() {
        return tc -> tc.fireEvent(new Event(tc, Special.SEQUENCE));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TrainsCycle))
            return false;
        return this == obj;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + ((this.id != null) ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * corrects position of an item.
     *
     * @param item item to be corrected
     */
    public void correctItem(TrainsCycleItem item) {
        int oldIndex = items.indexOf(item);
        items.remove(oldIndex);
        int newIndex = this.addItemImpl(item);
        if (oldIndex != newIndex) {
            this.listenerSupport.fireEvent(new Event(this, Event.Type.MOVED, item, ListData.createData(oldIndex, newIndex)));
        }
    }

    public TrainsCycleItem getFirstItem() {
        return items.isEmpty() ? null : items.get(0);
    }

    public TrainsCycleItem getLastItem() {
        return items.isEmpty() ? null : items.get(items.size() - 1);
    }

    public TrainsCycleItem getNextItem(TrainsCycleItem item) {
        int ind = items.indexOf(item);
        if (ind == -1) {
            return null;
        }
        ind++;
        if (ind >= items.size()) {
            return null;
        }
        return items.get(ind);
    }

    public TrainsCycleItem getNextItemCyclic(TrainsCycleItem item) {
        TrainsCycleItem nextItem = getNextItem(item);
        if (nextItem == null) {
            TrainsCycle nextCycle = getNext();
            while (nextCycle.isEmpty()) nextCycle = nextCycle.getNext();
            nextItem = nextCycle.getFirstItem();
        }
        return nextItem;
    }

    public TrainsCycleItem getPreviousItem(TrainsCycleItem item) {
        int ind = items.indexOf(item);
        if (ind == -1) {
            return null;
        }
        ind--;
        if (ind < 0) {
            return null;
        }
        return items.get(ind);
    }

    public TrainsCycleItem getPreviousItemCyclic(TrainsCycleItem item) {
        TrainsCycleItem previousItem = getPreviousItem(item);
        if (previousItem == null) {
            TrainsCycle previousCycle = getPrevious();
            while (previousCycle.isEmpty()) previousCycle = previousCycle.getPrevious();
            previousItem = previousCycle.getLastItem();
        }
        return previousItem;
    }

    public void addItem(TrainsCycleItem item) {
        addItemImpl(item);
        item.getTrain().addCycleItem(item);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.ADDED, item));
    }

    private int addItemImpl(TrainsCycleItem item) {
        int index = 0;
        for (TrainsCycleItem currentItem : items) {
            if (currentItem.getNormalizedStartTime() > item.getNormalizedStartTime()) {
                break;
            }
            index++;
        }
        items.add(index, item);
        return index;
    }

    public void removeItem(TrainsCycleItem item) {
        item.getTrain().removeCycleItem(item);
        items.remove(item);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.REMOVED, item));
    }

    public TrainsCycleItem removeItem(int index) {
        TrainsCycleItem item = items.remove(index);
        item.getTrain().removeCycleItem(item);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.REMOVED, item));
        return item;
    }

    public void replaceItem(TrainsCycleItem newItem, TrainsCycleItem oldItem) {
        if (newItem.getTrain() != oldItem.getTrain() || newItem.getCycle() != this || oldItem.getCycle() != this)
            throw new IllegalArgumentException("Illegal argument.");
        this.items.set(this.items.indexOf(oldItem), newItem);
        Train t = newItem.getTrain();
        t.replaceCycleItem(newItem, oldItem);
        this.listenerSupport.fireEvent(new Event(this, Event.Type.REPLACED, newItem, ListData.createData(oldItem, newItem)));
        this.correctItem(newItem);
    }

    public List<TrainsCycleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public TrainsCycleType getType() {
        return type;
    }

    public void setType(TrainsCycleType type) {
        if (!ObjectsUtil.compareWithNull(type, this.type)) {
            TrainsCycleType oldType = this.type;
            this.type = type;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TYPE, oldType, type)));
        }
    }

    public Collection<TrainsCycleItem> getItemsForTrain(Train train) {
        return Collections2.filter(items, item -> item.getTrain() == train);
    }

    @Override
    public Iterator<TrainsCycleItem> iterator() {
        return items.iterator();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        for (TrainsCycleItem item : items) {
            item.getTrain().removeCycleItem(item);
        }
    }

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    void fireEvent(Event event) {
        this.listenerSupport.fireEvent(event);
    }

    /**
     * accepts visitor
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
