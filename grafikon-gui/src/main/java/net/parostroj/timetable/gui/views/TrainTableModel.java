/*
 * TrainTableMode.java
 *
 * Created on 31.8.2007, 13:19:10
 */
package net.parostroj.timetable.gui.views;

import java.text.ParseException;

import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.utils.TimeUtil;

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
    private TimeConverter converter;

    public TrainTableModel(ApplicationModel model, Train train) {
        this.setTrain(train);
        this.model = model;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        if (this.editBlock)
            return;
        this.train = train;
        if (train != null) {
            this.lastRow = train.getTimeIntervalList().size() - 1;
            this.converter = train.getTrainDiagram().getTimeConverter();
        } else {
        	this.converter = null;
        }

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
                    retValue = converter.convertIntToText(interval.getStart(), true);
                break;
            // departure
            case END:
                if (!interval.isLast())
                    retValue = converter.convertIntToText(interval.getEnd(), true);
                break;
            // stop time
            case STOP:
                if (interval.getOwner() instanceof Node && rowIndex != 0 && rowIndex != lastRow
                        && ((Node)interval.getOwner()).getType() != NodeType.SIGNAL)
                	retValue = converter.convertIntToMinutesText(interval.getLength());
                break;
            // speed
            case SPEED:
                if (interval.getOwner() instanceof Line)
                    retValue = Integer.valueOf(interval.getSpeed());
                break;
            // added time
            case ADDED_TIME:
                if (interval.isLineOwner() && interval.getAddedTime() != 0)
                	retValue = converter.convertIntToMinutesText(interval.getAddedTime());
                break;
            // platform
            case PLATFORM:
                if (interval.getOwner() instanceof Node) {
                    if (((Node)interval.getOwner()).getType() != NodeType.SIGNAL)
                        retValue = interval.getTrack();
                } else if (interval.getOwner() instanceof Line) {
                    // only for more than one track per line
                    if (((Line)interval.getOwner()).getTracks().size() > 1) {
                        return interval.getTrack();
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
                retValue = interval.getAttribute(TimeInterval.ATTR_COMMENT);
                break;
            case OCCUPIED_ENTRY:
                Boolean value = (Boolean)interval.getAttribute(TimeInterval.ATTR_OCCUPIED);
                retValue = Boolean.TRUE.equals(value);
                break;
            case SHUNT:
                value = (Boolean)interval.getAttribute(TimeInterval.ATTR_SHUNT);
                retValue = Boolean.TRUE.equals(value);
                break;
            case COMMENT_SHOWN:
                value = (Boolean)interval.getAttribute(TimeInterval.ATTR_COMMENT_SHOWN);
                retValue = Boolean.TRUE.equals(value);
                break;
            case REAL_STOP:
                if (interval.getOwner() instanceof Node && rowIndex != 0 && rowIndex != lastRow
                        && ((Node)interval.getOwner()).getType() != NodeType.SIGNAL) {
                    int stop = interval.getLength() / 60;
                    // celculate with time scale ...
                    Double timeScale = (Double)model.getDiagram().getAttribute(TrainDiagram.ATTR_TIME_SCALE);
                    retValue = Double.valueOf(stop / timeScale.doubleValue());
                }
                break;
            case WEIGHT:
                // weight info
                if (interval.isLineOwner()) {
                    retValue = TrainsHelper.getWeight(interval);
                }
                break;
            case LENGTH:
                // length info
                retValue = TrainsHelper.getLength(interval);
                break;
            case SET_SPEED:
                // train speed
                retValue = interval.getAttribute(TimeInterval.ATTR_SET_SPEED);
                break;
            case IGNORE_LENGTH:
                // ignore station length
                value = (Boolean) interval.getAttribute(TimeInterval.ATTR_IGNORE_LENGTH);
                retValue = Boolean.TRUE.equals(value);
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
        TimeInterval interval = train.getTimeIntervalList().get(rowIndex);
        TrainTableColumn column = TrainTableColumn.getColumn(columnIndex);
        switch (column) {
            case START:
                time = converter.convertTextToInt((String) aValue);
                if (time != -1) {
                    int oldTime = TimeUtil.normalizeTime(interval.getStart());
                    int newTime = TimeUtil.normalizeTime(time);
                    int newStartTime = TimeUtil.normalizeTime(train.getStartTime() + (newTime - oldTime));
                    train.move(newStartTime);
                    this.fireTableRowsUpdated(0, lastRow);
                }
                break;
            case END:
                // departure
                time = converter.convertTextToInt((String) aValue);
                if (time != -1) {
                    if (rowIndex == 0) {
                        train.move(time);
                        this.fireTableDataChanged();
                    } else {
                        int start = TimeUtil.normalizeTime(interval.getStart());
                        time = TimeUtil.normalizeTime(time);
                        if (time < start)
                        	time += TimeInterval.DAY;
                        int newStop = time - start;
                        if (newStop >= 0) {
                            train.changeStopTime(interval, newStop);
                            this.fireTableRowsUpdated(rowIndex - 1, lastRow);
                        }
                    }
                }
                break;
            case STOP:
                // stop time
                try {
                    time = converter.convertMinutesTextToInt((String) aValue);
                } catch (ParseException e) {
                    // wrong conversion doesn't change anything
                    time = -1;
                }
                if (time >= 0) {
                    train.changeStopTime(interval, time);
                    this.fireTableRowsUpdated(rowIndex - 1, lastRow);
                }
                break;
            case SPEED:
                // velocity
                int velocity = ((Integer)aValue).intValue();
                if (velocity > 0) {
                    train.changeSpeedAndAddedTime(interval, velocity, interval.getAddedTime());
                    this.fireTableRowsUpdated(rowIndex - 2 >= 0 ? rowIndex - 2 : 0, lastRow);
                }
                break;
            case ADDED_TIME:
                // added time
                if (aValue != null) {
                    int addedTime;
                    try {
                        addedTime = converter.convertMinutesTextToInt((String) aValue);
                    } catch (ParseException e) {
                        // wrong conversion doesn't change anything
                        addedTime = -1;
                    }
                    if (addedTime >= 0)
                        train.changeSpeedAndAddedTime(interval, interval.getSpeed(), addedTime);
                } else {
                    train.changeSpeedAndAddedTime(interval, interval.getSpeed(), 0);
                }
                this.fireTableRowsUpdated(rowIndex, lastRow);
                break;
            case PLATFORM:
                // platform
                Track track = (Track) aValue;
                if (interval.getOwner() instanceof Node) {
                    NodeTrack newTrack = (NodeTrack) track;
                    if (newTrack != null) {
                        train.changeNodeTrack(interval, newTrack);
                        this.fireTableRowsUpdated(rowIndex, rowIndex);
                    }
                } else if (interval.getOwner() instanceof Line) {
                    LineTrack newTrack = (LineTrack) track;
                    if (newTrack != null) {
                        train.changeLineTrack(interval, newTrack);
                        this.fireTableRowsUpdated(rowIndex, rowIndex);
                    }
                }
                break;
            case COMMENT:
                // comment
                String commentStr = Conversions.checkAndTrim((String) aValue);
                interval.getAttributes().setRemove(TimeInterval.ATTR_COMMENT, commentStr);
                break;
            case OCCUPIED_ENTRY:
                // entry of the occupied track
                interval.getAttributes().setBool(TimeInterval.ATTR_OCCUPIED, (Boolean) aValue);
                break;
            case SHUNT:
                // entry shunting on the far side
                interval.getAttributes().setBool(TimeInterval.ATTR_SHUNT, (Boolean) aValue);
                break;
            case COMMENT_SHOWN:
                // entry shunting on the far side
                interval.getAttributes().setBool(TimeInterval.ATTR_COMMENT_SHOWN, (Boolean) aValue);
                break;
            case SET_SPEED:
                // train speed
                Integer trainSpeed = (Integer) aValue;
                if (trainSpeed != null && trainSpeed > 0) {
                    if (trainSpeed > train.getTopSpeed()) {
                        trainSpeed = train.getTopSpeed();
                    }
                } else {
                    trainSpeed = null;
                }
                interval.getAttributes().setRemove(TimeInterval.ATTR_SET_SPEED, trainSpeed);
                break;
            case IGNORE_LENGTH:
                // ignore length of the station in computation
                interval.getAttributes().setBool(TimeInterval.ATTR_IGNORE_LENGTH, (Boolean) aValue);
                this.fireTableRowsUpdated(rowIndex, rowIndex);
                break;
            default:
                break;
        }
        editBlock = false;
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }
}
