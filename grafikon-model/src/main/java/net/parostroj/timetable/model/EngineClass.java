package net.parostroj.timetable.model;

import java.util.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Engine class. It contains table with weight information for each
 * line class.
 * 
 * @author jub
 */
public class EngineClass implements ObjectWithId {

    private static final class EmptyWeightTableRow extends WeightTableRow {

        public EmptyWeightTableRow() {
            super(Line.UNLIMITED_SPEED);
        }

        @Override
        public void removeWeightInfo(LineClass lineClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSpeed(int speed) {
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

    public EngineClass(String id, String name) {
        this.name = name;
        this.id = id;
        this.weightTable = new LinkedList<WeightTableRow>();
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

    public void addWeightTableRow(WeightTableRow row) {
        ListIterator<WeightTableRow> i = weightTable.listIterator();
        while (i.hasNext()) {
            WeightTableRow currentRow = i.next();
            if (row.getSpeed() < currentRow.getSpeed()) {
                i.previous();
                i.add(row);
                return;
            }
        }
        weightTable.add(row);
    }

    public void removeWeightTableRowForSpeed(int speed) {
        for (Iterator<WeightTableRow> i = weightTable.iterator(); i.hasNext();) {
            WeightTableRow row = i.next();
            if (row.getSpeed() == speed) {
                i.remove();
            }
        }
    }

    public void removeWeightTableRow(int position) {
        weightTable.remove(position);
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
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
