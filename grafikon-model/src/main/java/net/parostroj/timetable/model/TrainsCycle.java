/*
 * TrainsCycle.java
 *
 * Created on 11.9.2007, 20:30:58
 */
package net.parostroj.timetable.model;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.TransformUtil;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Trains cycle.
 *
 * @author jub
 */
public class TrainsCycle implements AttributesHolder, ObjectWithId, Iterable<TrainsCycleItem>, Visitable, TrainsCycleAttributes, TrainDiagramPart {

    private final String id;
    private final TrainDiagram diagram;
    private String name;
    private String description;
    private TrainsCycleType type;
    private Attributes attributes;
    private final List<TrainsCycleItem> items;
    private final GTListenerSupport<TrainsCycleListener, TrainsCycleEvent> listenerSupport;
    private AttributesListener attributesListener;
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
        this.setAttributes(new Attributes());
        this.type = type;
        this.items = new LinkedList<TrainsCycleItem>();
        listenerSupport = new GTListenerSupport<TrainsCycleListener, TrainsCycleEvent>(
                (listener, event) -> listener.trainsCycleChanged(event));
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
            this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange(ATTR_DESCRIPTION,
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
            this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange(ATTR_NAME, oldName, name)));
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
        TrainsCycle next = getNext();
        TrainsCycle afterNext = next.getNext();
        if (next != this && afterNext != this) {
            // more than 2 circulations
            TrainsCycle previous = getPrevious();
            connectTwo(previous, next);
            connectTwo(next, this);
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
            function.apply(result, current);
            current = current.getNext();
        } while (current != this);
        return result;
    }

    private static Consumer<TrainsCycle> getSendChangedSequence() {
        return tc -> {
            tc.fireEvent(new TrainsCycleEvent(tc, GTEventType.CYCLE_SEQUENCE));
        };
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TrainsCycle))
            return false;
        return this == obj;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + ((this.id != null) ? this.id.hashCode() : 0);
        return hash;
    }

    public List<Tuple<TrainsCycleItem>> checkConflicts() {
        List<Tuple<TrainsCycleItem>> conflicts = null;
        Iterator<TrainsCycleItem> i = items.iterator();
        TrainsCycleItem last = null;
        if (i.hasNext()) {
            last = i.next();
        }
        while (i.hasNext()) {
            TrainsCycleItem current = i.next();
            if (last.getToInterval().getOwner() != current.getFromInterval().getOwner() || last.getEndTime() >= current.getStartTime()) {
                if (conflicts == null) {
                    conflicts = new LinkedList<Tuple<TrainsCycleItem>>();
                }
                conflicts.add(new Tuple<TrainsCycleItem>(last, current));
            }
            last = current;
        }
        if (conflicts == null) {
            conflicts = Collections.emptyList();
        }
        return conflicts;
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
            this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_MOVED, item, item));
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
            nextItem = getNext().getFirstItem();
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
            previousItem = getPrevious().getLastItem();
        }
        return previousItem;
    }

    public void addItem(TrainsCycleItem item) {
        addItemImpl(item);
        item.getTrain().addCycleItem(item);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_ADDED, null, item));
    }

    private int addItemImpl(TrainsCycleItem item) {
        int index = 0;
        for (TrainsCycleItem currentItem : items) {
            if (currentItem.getStartTime() > item.getStartTime()) {
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
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_REMOVED, item, null));
    }

    public TrainsCycleItem removeItem(int index) {
        TrainsCycleItem item = items.remove(index);
        item.getTrain().removeCycleItem(item);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_REMOVED, item, null));
        return item;
    }

    public void replaceItem(TrainsCycleItem newItem, TrainsCycleItem oldItem) {
        if (newItem.getTrain() != oldItem.getTrain() || newItem.getCycle() != this || oldItem.getCycle() != this)
            throw new IllegalArgumentException("Illegal argument.");
        this.items.set(this.items.indexOf(oldItem), newItem);
        Train t = newItem.getTrain();
        t.removeCycleItem(oldItem);
        t.addCycleItem(newItem);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_UPDATED, oldItem, newItem));
        this.correctItem(newItem);
    }

    public List<TrainsCycleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = (attrs, change) -> listenerSupport
                .fireEvent(new TrainsCycleEvent(TrainsCycle.this, change));
        this.attributes.addListener(attributesListener);
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

    public TrainsCycleType getType() {
        return type;
    }

    public void setType(TrainsCycleType type) {
        if (!ObjectsUtil.compareWithNull(type, this.type)) {
            TrainsCycleType oldType = this.type;
            this.type = type;
            this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange(ATTR_TYPE, oldType, type)));
        }
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

    public void addListener(TrainsCycleListener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeListener(TrainsCycleListener listener) {
        listenerSupport.removeListener(listener);
    }

    void fireEvent(TrainsCycleEvent event) {
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
