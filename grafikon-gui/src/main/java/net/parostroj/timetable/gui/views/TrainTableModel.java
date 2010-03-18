/*
 * TrainTableMode.java
 *
 * Created on 31.8.2007, 13:19:10
 */
package net.parostroj.timetable.gui.views;

import javax.swing.table.AbstractTableModel;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * Table model for train.
 *
 * @author jub
 */
class TrainTableModel extends AbstractTableModel {

    /** Train. */
    private Train train;
    
    private int lastRow;
    
    private ApplicationModel model;

    private boolean editBlock;

    public TrainTableModel(ApplicationModel model, Train train) {
        this.setTrain(train);
        this.model = model;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        if (editBlock)
            return;
        this.train = train;
        if (train != null)
            lastRow = train.getTimeIntervalList().size() - 1;

        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return (train != null) ? (lastRow + 1) : 0;
    }

    @Override
    public int getColumnCount() {
        return TrainTableColumn.values().length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return TrainTableColumn.getColumn(columnIndex).getClazz();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        TimeInterval interval = train.getTimeIntervalList().get(rowIndex);
        // do not alow edit signals
        if (rowIndex % 2 == 0) {
            Node node = (Node)interval.getOwner();
            if (node.getType() == NodeType.SIGNAL)
                return false;
        }
        return TrainTableColumn.getColumn(columnIndex).isAllowedToEdit(rowIndex, lastRow, interval);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TimeInterval interval = train.getTimeIntervalList().get(rowIndex);
        Object retValue = null;
        TrainTableColumn column = TrainTableColumn.getColumn(columnIndex);
        switch (column) {
            // node name
            case NODE:
                if (interval.getOwner() instanceof Node)
                    retValue = ((Node)interval.getOwner()).getName();
                else
                    retValue = "";
                break;
            // arrival
            case START:
                if (!interval.isFirst())
                    retValue = TimeConverter.convertFromIntToText(interval.getStart());
                break;
            // departure
            case END:
                if (!interval.isLast())
                    retValue = TimeConverter.convertFromIntToText(interval.getEnd());
                break;
            // stop time
            case STOP:
                if (interval.getOwner() instanceof Node && rowIndex != 0 && rowIndex != lastRow
                        && ((Node)interval.getOwner()).getType() != NodeType.SIGNAL)
                    retValue = Integer.valueOf(interval.getLength() / 60);
                break;
            // speed
            case SPEED:
                if (interval.getOwner() instanceof Line)
                    retValue = Integer.valueOf(interval.getSpeed());
                break;
            // platform
            case PLATFORM:
                if (interval.getOwner() instanceof Node) {
                    if (((Node)interval.getOwner()).getType() != NodeType.SIGNAL)
                        retValue = interval.getTrack().getNumber();
                } else if (interval.getOwner() instanceof Line) {
                    // only for more than one track per line
                    if (((Line)interval.getOwner()).getTracks().size() > 1) {
                        return interval.getTrack().getNumber();
                    }
                }
                break;
            // problems
            case CONFLICTS:
                StringBuilder builder = new StringBuilder();
                for (TimeInterval overlap : interval.getOverlappingIntervals()) {
                    if (builder.length() != 0)
                        builder.append(", ");
                    builder.append(overlap.getTrain().getName());
                }
                retValue = builder.toString();
                break;
            // comment
            case COMMENT:
                retValue = interval.getAttribute("comment");
                break;
            case OCCUPIED_ENTRY:
                Boolean value = (Boolean)interval.getAttribute("occupied");
                retValue = Boolean.TRUE.equals(value);
                break;
            case SHUNT:
                value = (Boolean)interval.getAttribute("shunt");
                retValue = Boolean.TRUE.equals(value);
                break;
            case COMMENT_SHOWN:
                value = (Boolean)interval.getAttribute("comment.shown");
                retValue = Boolean.TRUE.equals(value);
                break;
            case REAL_STOP:
                if (interval.getOwner() instanceof Node && rowIndex != 0 && rowIndex != lastRow
                        && ((Node)interval.getOwner()).getType() != NodeType.SIGNAL) {
                    int stop = interval.getLength() / 60;
                    // celculate with time scale ...
                    Double timeScale = (Double)model.getDiagram().getAttribute("time.scale");
                    retValue = Double.valueOf(stop / timeScale.doubleValue());
                }
                break;
            case WEIGHT:
                // weight info
                if (interval.isLineOwner()) {
                    retValue = TrainsHelper.getWeightWithAttribute(interval);
                }
                break;
            case LENGTH:
                // length info
                if (interval.isLineOwner()) {
                    retValue = TrainsHelper.convertWeightToLength(train, model.getDiagram(), TrainsHelper.getWeightWithAttribute(interval));
                } else if (interval.isNodeOwner()) {
                    retValue = (Integer)interval.getOwnerAsNode().getAttribute("length");
                }
                break;
            // default (should not be reached)
            default:
                // nothing
                assert false : "Unexpected column";
                break;
        }
        return retValue;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int time = 0;
        editBlock = true;
        TimeInterval interval = null;
        TrainTableColumn column = TrainTableColumn.getColumn(columnIndex);
        switch (column) {
            case END:
                // departure
                time = TimeConverter.convertFromTextToInt((String)aValue);
                if (time != -1) {
                    if (rowIndex == 0) {
                        train.move(time);
                        this.fireTableDataChanged();
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                    } else {
                        interval = train.getTimeIntervalList().get(rowIndex);
                        int newStop = time - interval.getStart();
                        if (newStop >= 0) {
                            train.changeStopTime(interval, newStop);
                            this.fireTableRowsUpdated(rowIndex, lastRow);
                            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                        }
                    }
                }
                break;
            case STOP:
                // stop time
                time = ((Integer)aValue).intValue() * 60;
                if (time >= 0) {
                    interval = train.getTimeIntervalList().get(rowIndex);
                    train.changeStopTime(interval, time);
                    this.fireTableRowsUpdated(rowIndex, lastRow);
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                }
                break;
            case SPEED:
                // velocity
                int velocity = ((Integer)aValue).intValue();
                if (velocity > 0) {
                    interval = train.getTimeIntervalList().get(rowIndex);
                    train.changeSpeed(interval, velocity);
                    this.fireTableRowsUpdated(rowIndex, lastRow);
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                }
                break;
            case PLATFORM:
                // platform
                String platform = (String)aValue;
                interval = train.getTimeIntervalList().get(rowIndex);
                if (interval.getOwner() instanceof Node) {
                    Node node = (Node)interval.getOwner();
                    NodeTrack newTrack = node.getNodeTrackByNumber(platform);
                    if (newTrack != null) {
                        train.changeNodeTrack(interval, newTrack);
                        this.fireTableRowsUpdated(rowIndex, rowIndex);
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                    }
                } else if (interval.getOwner() instanceof Line) {
                    Line line = (Line)interval.getOwner();
                    LineTrack newTrack = line.getLineTrackByNumber(platform);
                    if (newTrack != null) {
                        train.changeLineTrack(interval, newTrack);
                        this.fireTableRowsUpdated(rowIndex, rowIndex);
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                    }
                }
                break;
            case COMMENT:
                // comment
                interval = train.getTimeIntervalList().get(rowIndex);
                String commentStr = (String)aValue;
                if ("".equals(commentStr))
                    commentStr = null;
                if (commentStr != null)
                    interval.setAttribute("comment", (String)aValue);
                else
                    interval.removeAttribute("comment");
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN_ATTRIBUTE, model, train));
                break;
            case OCCUPIED_ENTRY:
                // entry of the occupied track
                interval = train.getTimeIntervalList().get(rowIndex);
                if (Boolean.TRUE.equals(aValue)) {
                    interval.setAttribute("occupied", aValue);
                } else {
                    interval.removeAttribute("occupied");
                }
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN_ATTRIBUTE, model, train));
                break;
            case SHUNT:
                // entry shunting on the far side
                interval = train.getTimeIntervalList().get(rowIndex);
                if (Boolean.TRUE.equals(aValue)) {
                    interval.setAttribute("shunt", aValue);
                } else {
                    interval.removeAttribute("shunt");
                }
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN_ATTRIBUTE, model, train));
                break;
            case COMMENT_SHOWN:
                // entry shunting on the far side
                interval = train.getTimeIntervalList().get(rowIndex);
                if (Boolean.TRUE.equals(aValue)) {
                    interval.setAttribute("comment.shown", aValue);
                } else {
                    interval.removeAttribute("comment.shown");
                }
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN_ATTRIBUTE, model, train));
                break;
        }
        editBlock = false;
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }
}
