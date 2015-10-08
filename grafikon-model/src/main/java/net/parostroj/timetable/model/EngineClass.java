package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Engine class. It contains table with weight information for each
 * line class.
 *
 * @author jub
 */
public class EngineClass implements ObjectWithId, Visitable, Observable, EngineClassAttributes {

    private final String id;
    private String name;
    private final List<WeightTableRow> weightTable;
    private final ListenerSupport listenerSupport;

    public EngineClass(String id, String name) {
        this.name = name;
        this.id = id;
        this.weightTable = new LinkedList<WeightTableRow>();
        this.listenerSupport = new ListenerSupport();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.fireEvent(new Event(this, new AttributeChange(ATTR_NAME, oldName, name)));
    }

    @Override
    public String getId() {
        return id;
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
        ListIterator<WeightTableRow> i = weightTable.listIterator();
        while (i.hasNext()) {
            WeightTableRow row = i.next();
            if (speed <= row.getSpeed()) {
                return row;
            }
        }
        // return null for speed not in table
        return null;
    }

    public WeightTableRow getWeightTableRowForSpeedExact(int speed) {
        ListIterator<WeightTableRow> i = weightTable.listIterator();
        while (i.hasNext()) {
            WeightTableRow row = i.next();
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
            return weightTable.get(weightTable.size() - 1);
    }

    public List<WeightTableRow> getWeightTable() {
        return Collections.unmodifiableList(weightTable);
    }

    @Override
    public String toString() {
        return name;
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
        listenerSupport.fireEvent(event);
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
}
