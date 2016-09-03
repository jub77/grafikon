package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.ListData;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.ListenerSupport;

/**
 * Category of train types - freight, passenger ...
 *
 * @author jub
 */
public class TrainTypeCategory implements ObjectWithId, Observable, AttributesHolder, TrainTypeCategoryAttributes {

    private final String id;

    private final Attributes attributes;

    private List<PenaltyTableRow> penaltyRows;

    private final ListenerSupport listenerSupport;

    public TrainTypeCategory(String id) {
        this.id = id;
        this.penaltyRows = new ArrayList<>();
        this.listenerSupport = new ListenerSupport();
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(TrainTypeCategory.this, change)));
    }

    public String getKey() {
        return attributes.get(ATTR_KEY, String.class);
    }

    public void setKey(String key) {
        attributes.setRemove(ATTR_KEY, key);
    }

    public LocalizedString getName() {
        return attributes.get(ATTR_NAME, LocalizedString.class);
    }

    public void setName(LocalizedString name) {
        attributes.setRemove(ATTR_NAME, name);;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public void addRow(PenaltyTableRow row) {
        ListIterator<PenaltyTableRow> i = penaltyRows.listIterator();
        while (i.hasNext()) {
            PenaltyTableRow currentRow = i.next();
            if (row.getSpeed() < currentRow.getSpeed()) {
                i.previous();
                i.add(row);
                listenerSupport.fireEvent(new Event(this, Event.Type.ADDED, row));
                return;
            } else if (row.getSpeed() == currentRow.getSpeed()) {
                i.set(row);
                listenerSupport.fireEvent(new Event(this, Event.Type.REPLACED, row, ListData.createData(currentRow, row)));
                return;
            }
        }
        penaltyRows.add(row);
        listenerSupport.fireEvent(new Event(this, Event.Type.ADDED, row));
    }

    public PenaltyTableRow removeRowForSpeed(int speed) {
        for (Iterator<PenaltyTableRow> i = penaltyRows.iterator(); i.hasNext();) {
            PenaltyTableRow row = i.next();
            if (row.getSpeed() == speed) {
                i.remove();
                listenerSupport.fireEvent(new Event(this, Event.Type.REMOVED, row));
                return row;
            }
        }
        return null;
    }

    public PenaltyTableRow removeRow(int position) {
        PenaltyTableRow row = penaltyRows.remove(position);
        listenerSupport.fireEvent(new Event(this, Event.Type.REMOVED, row));
        return row;
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
        return getName().translate() + "<" + getKey() + ">";
    }
}
