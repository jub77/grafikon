package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.ObservableObject;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Engine class. It contains table with weight information for each
 * line class.
 *
 * @author jub
 */
public class EngineClass implements AttributesHolder, ObjectWithId, Visitable, ObservableObject,
        ItemCollectionObject, ObjectWithVersion {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_GROUP_KEY = "group.key";
    public static final String ATTR_VERSION = "version";

    private final String id;
    private final List<WeightTableRow> weightTable;
    private final Attributes attributes;
    private final ListenerSupport listenerSupport;

    private boolean events;

    public EngineClass(String id) {
        this.id = id;
        this.weightTable = new LinkedList<>();
        this.listenerSupport = new ListenerSupport();
        this.attributes = new Attributes(
                (attrs, change) -> this.fireEvent(new Event(EngineClass.this, change)));
    }

    public String getName() {
        return this.attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        this.attributes.setRemove(ATTR_NAME, name);
    }

    public String getGroupKey() {
        return this.getAttribute(ATTR_GROUP_KEY, String.class);
    }

    public void setGroupKey(String key) {
        this.setRemoveAttribute(ATTR_GROUP_KEY, key);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ModelVersion getVersion() {
        return getAttribute(ATTR_VERSION, ModelVersion.class, ModelVersion.initialModelVersion());
    }

    public WeightTableRow createWeightTableRow(int speed) {
        return new WeightTableRow(this, speed);
    }

    public void addWeightTableRow(WeightTableRow row) {
        ListIterator<WeightTableRow> i = weightTable.listIterator();
        while (i.hasNext()) {
            WeightTableRow currentRow = i.next();
            if (row.getSpeed() < currentRow.getSpeed()) {
                i.previous();
                i.add(row);
                this.fireEvent(new Event(this, Event.Type.ADDED, row));
                return;
            }
            if (row.getSpeed() == currentRow.getSpeed())
                // do not add duplicate row
                return;
        }
        weightTable.add(row);
        this.fireEvent(new Event(this, Event.Type.ADDED, row));
    }

    public void removeWeightTableRowForSpeed(int speed) {
        for (Iterator<WeightTableRow> i = weightTable.iterator(); i.hasNext();) {
            WeightTableRow row = i.next();
            if (row.getSpeed() == speed) {
                i.remove();
                this.fireEvent(new Event(this, Event.Type.REMOVED, row));
                return;
            }
        }
    }

    public void removeWeightTableRow(int position) {
        WeightTableRow removed = weightTable.remove(position);
        if (removed != null) {
            this.fireEvent(new Event(this, Event.Type.REMOVED, removed));
        }
    }

    public WeightTableRow getWeightTableRowForSpeed(int speed) {
        for (WeightTableRow row : weightTable) {
            if (speed <= row.getSpeed()) {
                return row;
            }
        }
        // return null for speed not in table
        return null;
    }

    public WeightTableRow getWeightTableRowForSpeedExact(int speed) {
        for (WeightTableRow row : weightTable) {
            if (speed == row.getSpeed()) {
                return row;
            }
        }
        return null;
    }

    public WeightTableRow getWeigthTableRowWithMaxSpeed() {
        if (weightTable.isEmpty())
            // return null
            return null;
        else
            // return last row (one with max speed)
            return weightTable.getLast();
    }

    public List<WeightTableRow> getWeightTable() {
        return Collections.unmodifiableList(weightTable);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * fires event.
     *
     * @param event event
     */
    protected void fireEvent(Event event) {
        if (events) {
            listenerSupport.fireEvent(event);
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

    public void removeAllListeners() {
        listenerSupport.removeAllListeners();
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public void added() {
        events = true;
    }

    @Override
    public void removed() {
        events = false;
    }
}
