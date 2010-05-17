/*
 * TrainsCycle.java
 * 
 * Created on 11.9.2007, 20:30:58
 */
package net.parostroj.timetable.model;

import java.util.*;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainsCycleEvent;
import net.parostroj.timetable.model.events.TrainsCycleListener;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Trains cycle.
 * 
 * @author jub
 */
public class TrainsCycle implements AttributesHolder, ObjectWithId, Iterable<TrainsCycleItem>, Visitable {

    private final String id;
    private String name;
    private String description;
    private TrainsCycleType type;
    private Attributes attributes;
    private List<TrainsCycleItem> items;
    private GTListenerSupport<TrainsCycleListener, TrainsCycleEvent> listenerSupport;

    /**
     * creates instance
     * 
     * @param id id
     * @param name name of the cycle
     * @param description description
     */
    public TrainsCycle(String id, String name, String description, TrainsCycleType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.attributes = new Attributes();
        this.type = type;
        this.items = new LinkedList<TrainsCycleItem>();
        listenerSupport = new GTListenerSupport<TrainsCycleListener, TrainsCycleEvent>(new GTEventSender<TrainsCycleListener, TrainsCycleEvent>() {

            @Override
            public void fireEvent(TrainsCycleListener listener, TrainsCycleEvent event) {
                listener.trainsCycleChanged(event);
            }
        });
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange("description", oldDescription, description)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange("name", oldName, name)));
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
        hash = 97 * hash + this.id != null ? this.id.hashCode() : 0;
        hash = 97 * hash + this.name != null ? this.name.hashCode() : 0;
        hash = 97 * hash + this.description != null ? this.description.hashCode() : 0;
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

    public void addItem(TrainsCycleItem item) {
        item.getTrain().addCycleItem(item);
        items.add(item);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_ADDED, null, item));
    }
    
    public void removeItem(TrainsCycleItem item) {
        item.getTrain().removeCycleItem(item);
        items.remove(item);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_REMOVED, item, null));
    }
    
    public void addItem(TrainsCycleItem item, int index) {
        item.getTrain().addCycleItem(item);
        items.add(index, item);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_ADDED, null, item));
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
        Train t = newItem.getTrain();
        t.removeCycleItem(oldItem);
        t.addCycleItem(newItem);
        this.items.set(this.items.indexOf(oldItem), newItem);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, GTEventType.CYCLE_ITEM_UPDATED, oldItem, newItem));
    }
    
    public List<TrainsCycleItem> getItems() {
        return Collections.unmodifiableList(items);
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
        Object oldValue = attributes.get(key);
        attributes.put(key, value);
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange(key, oldValue, value)));
    }

    @Override
    public Object removeAttribute(String key) {
        Object o = attributes.remove(key);
        if (o != null)
            this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange(key, o, null)));
        return o;
    }

    public TrainsCycleType getType() {
        return type;
    }

    public void setType(TrainsCycleType type) {
        TrainsCycleType oldType = this.type;
        this.type = type;
        this.listenerSupport.fireEvent(new TrainsCycleEvent(this, new AttributeChange("type", oldType, type)));
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
