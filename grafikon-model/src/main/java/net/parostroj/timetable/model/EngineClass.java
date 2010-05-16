package net.parostroj.timetable.model;

import java.util.*;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.EngineClassEvent;
import net.parostroj.timetable.model.events.EngineClassListener;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Engine class. It contains table with weight information for each
 * line class.
 * 
 * @author jub
 */
public class EngineClass implements ObjectWithId, Visitable {

    private static final class EmptyWeightTableRow extends WeightTableRow {

        public EmptyWeightTableRow() {
            super(null, Line.UNLIMITED_SPEED);
        }

        @Override
        public void removeWeightInfo(LineClass lineClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWeightInfo(LineClass lineClass, Integer weight) {
            throw new UnsupportedOperationException();
        }
    }

    private static final WeightTableRow EMPTY_ROW = new EmptyWeightTableRow();
    private final String id;
    private String name;
    private List<WeightTableRow> weightTable;
    private GTListenerSupport<EngineClassListener, EngineClassEvent> listenerSupport;

    public EngineClass(String id, String name) {
        this.name = name;
        this.id = id;
        this.weightTable = new LinkedList<WeightTableRow>();
        this.listenerSupport = new GTListenerSupport<EngineClassListener, EngineClassEvent>(new GTEventSender<EngineClassListener, EngineClassEvent>() {

            @Override
            public void fireEvent(EngineClassListener listener, EngineClassEvent event) {
                listener.engineClassChanged(event);
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.fireEvent(new EngineClassEvent(this, new AttributeChange("name", oldName, name)));
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
                this.fireEvent(new EngineClassEvent(this, row, EngineClassEvent.Type.ROW_ADDED));
                return;
            }
            if (row.getSpeed() == currentRow.getSpeed())
                // do not add duplicate row
                return;
        }
        weightTable.add(row);
        this.fireEvent(new EngineClassEvent(this, row, EngineClassEvent.Type.ROW_ADDED));
    }

    public void removeWeightTableRowForSpeed(int speed) {
        for (Iterator<WeightTableRow> i = weightTable.iterator(); i.hasNext();) {
            WeightTableRow row = i.next();
            if (row.getSpeed() == speed) {
                i.remove();
                this.fireEvent(new EngineClassEvent(this, row, EngineClassEvent.Type.ROW_REMOVED));
                return;
            }
        }
    }

    public void removeWeightTableRow(int position) {
        WeightTableRow removed = weightTable.remove(position);
        if (removed != null) {
            this.fireEvent(new EngineClassEvent(this, removed, EngineClassEvent.Type.ROW_REMOVED));
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
        // return empty row for speed not in table
        return EMPTY_ROW;
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
    protected void fireEvent(EngineClassEvent event) {
        listenerSupport.fireEvent(event);
    }

    public void addListener(EngineClassListener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeListener(EngineClassListener listener) {
        listenerSupport.removeListener(listener);
    }

    public void removeAllListeners() {
        listenerSupport.removeAllListeners();
    }
}
