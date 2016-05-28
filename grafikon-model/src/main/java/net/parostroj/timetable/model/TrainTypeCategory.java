package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.ListenerSupport;

/**
 * Category of train types - freight, passenger ...
 *
 * @author jub
 */
public class TrainTypeCategory implements ObjectWithId, Observable {

    private String id;
    private String name;
    private String key;

    private List<PenaltyTableRow> penaltyRows;

    private final ListenerSupport listenerSupport;

    public TrainTypeCategory(String id, String name, String key) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.listenerSupport = new ListenerSupport();
        this.penaltyRows = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void addRow(PenaltyTableRow row) {
        ListIterator<PenaltyTableRow> i = penaltyRows.listIterator();
        while (i.hasNext()) {
            PenaltyTableRow currentRow = i.next();
            if (row.getSpeed() < currentRow.getSpeed()) {
                i.previous();
                i.add(row);
                return;
            }
        }
        penaltyRows.add(row);
    }

    public void removeRowForSpeed(int speed) {
        for (Iterator<PenaltyTableRow> i = penaltyRows.iterator(); i.hasNext();) {
            PenaltyTableRow row = i.next();
            if (row.getSpeed() == speed) {
                i.remove();
            }
        }
    }

    public PenaltyTableRow removeRow(int position) {
        return penaltyRows.remove(position);
    }

    public PenaltyTableRow getRowForSpeed(int speed) {
        // zero row is special case
        if (speed == 0)
            return PenaltyTableRow.ZERO_ROW;
        // other rows
        ListIterator<PenaltyTableRow> i = penaltyRows.listIterator();
        while (i.hasNext()) {
            PenaltyTableRow row = i.next();
            if (speed <= row.getSpeed()) {
                return row;
            }
        }
        // otherwise return null
        return null;
    }

    public PenaltyTableRow getRowForSpeedExact(int speed) {
        ListIterator<PenaltyTableRow> i = penaltyRows.listIterator();
        while (i.hasNext()) {
            PenaltyTableRow row = i.next();
            if (speed == row.getSpeed()) {
                return row;
            }
        }
        return null;
    }

    public List<PenaltyTableRow> getPenaltyRows() {
        return Collections.unmodifiableList(penaltyRows);
    }

    /**
     * adds listener.
     *
     * @param listener listener
     */
    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener.
     *
     * @param listener listener
     */
    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public String toString() {
        return name + "<" + key + ">";
    }
}
