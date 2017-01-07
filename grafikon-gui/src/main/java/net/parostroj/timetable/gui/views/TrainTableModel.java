/*
 * TrainTableMode.java
 *
 * Created on 31.8.2007, 13:19:10
 */
package net.parostroj.timetable.gui.views;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import net.parostroj.timetable.actions.*;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ObjectsUtil;
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
    private boolean editBlock;
    private TimeConverter converter;

    public TrainTableModel(ApplicationModel model, Train train) {
        this.setTrain(train);
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
            this.converter = train.getDiagram().getTimeConverter();
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
            Node node = interval.getOwnerAsNode();
            if (node.getType() == NodeType.SIGNAL && columnIndex != TrainTableColumn.PLATFORM.ordinal()) {
                return false;
            }
            if (columnIndex == TrainTableColumn.MANAGED_FREIGHT.ordinal()) {
                if (!train.isManagedFreight() || (interval.getLength() == 0 && rowIndex != 0 && rowIndex != lastRow)) {
                    return false;
                }
            }
            if (columnIndex == TrainTableColumn.REGION_CENTER_TRANSFER.ordinal() && rowIndex != 0) {
                return interval.getOwnerAsNode().isCenterOfRegions();
            }
        }
        return TrainTableColumn.getColumn(columnIndex).isAllowedToEdit(rowIndex, lastRow, interval);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TimeInterval interval = train.getTimeIntervalList().get(rowIndex);
        TrainDiagram diagram = train.getDiagram();
        Object retValue = null;
        TrainTableColumn column = TrainTableColumn.getColumn(columnIndex);
        switch (column) {
            // node name
            case NODE:
                if (interval.isNodeOwner()) {
                    retValue = interval.getOwnerAsNode().getName();
                } else {
                    retValue = "";
                }
                break;
            // arrival
            case START:
                if (!interval.isFirst()) {
                    retValue = converter.convertIntToText(interval.getStart(), true);
                }
                break;
            // departure
            case END:
                if (!interval.isLast()) {
                    retValue = converter.convertIntToText(interval.getEnd(), true);
                }
                break;
            // stop time
            case STOP:
                if (interval.isNodeOwner() && rowIndex != 0 && rowIndex != lastRow
                        && interval.getOwnerAsNode().getType() != NodeType.SIGNAL) {
                    retValue = converter.convertIntToMinutesText(interval.getLength());
                }
                break;
            // speed
            case SPEED_LIMIT:
                if (interval.isLineOwner()) {
                    retValue = interval.getSpeedLimit();
                }
                break;
            // used speed
            case SPEED:
                retValue = interval.getSpeed();
                break;
            // added time
            case ADDED_TIME:
                if (interval.isLineOwner() && interval.getAddedTime() != 0) {
                    retValue = converter.convertIntToMinutesText(interval.getAddedTime());
                }
                break;
            // platform
            case PLATFORM:
                if (interval.isNodeOwner()) {
                    if (interval.getOwnerAsNode().getTracks().size() > 1) {
                        retValue = interval.getTrack();
                    }
                } else if (interval.isLineOwner()) {
                    // only for more than one track per line
                    if (interval.getOwnerAsLine().getTracks().size() > 1) {
                        return interval.getTrack();
                    }
                }
                break;
            // problems
            case CONFLICTS:
                StringBuilder builder = new StringBuilder();
                for (TimeInterval overlap : interval.getOverlappingIntervals()) {
                    if (builder.length() != 0) {
                        builder.append(", ");
                    }
                    builder.append(overlap.getTrain().getDefaultName());
                }
                retValue = builder.toString();
                break;
            // comment
            case COMMENT:
                LocalizedString lComment = interval.getComment();
                retValue = lComment == null ? null : lComment.getDefaultString();
                break;
            case OCCUPIED_ENTRY:
                retValue = interval.getAttributes().getBool(TimeInterval.ATTR_OCCUPIED);
                break;
            case SHUNT:
                retValue = interval.getAttributes().getBool(TimeInterval.ATTR_SHUNT);
                break;
            case COMMENT_SHOWN:
                retValue = interval.getAttributes().getBool(TimeInterval.ATTR_COMMENT_SHOWN);
                break;
            case REAL_STOP:
                if (interval.isNodeOwner() && rowIndex != 0 && rowIndex != lastRow
                        && interval.getOwnerAsNode().getType() != NodeType.SIGNAL) {
                    int stop = interval.getLength() / 60;
                    // celculate with time scale ...
                    Double timeScale = diagram.getAttributes().get(TrainDiagram.ATTR_TIME_SCALE, Double.class);
                    retValue = stop / timeScale;
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
                retValue = interval.getAttribute(TimeInterval.ATTR_SET_SPEED, Object.class);
                break;
            case IGNORE_LENGTH:
                // ignore station length
                retValue = interval.getAttributes().getBool(TimeInterval.ATTR_IGNORE_LENGTH);
                break;
            case MANAGED_FREIGHT:
                // managed freight
                retValue = false;
                if (train.isManagedFreight() && interval.isNodeOwner()) {
                    retValue = (interval.getLength() > 0 || rowIndex == 0 || rowIndex == lastRow) && !interval.getAttributes().getBool(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
                }
                break;
            case REGION_CENTER_TRANSFER:
                // transfer in region center
                retValue = false;
                if (interval.isNodeOwner()) {
                    if (interval.getOwnerAsNode().isCenterOfRegions() && !interval.getAttributeAsBool(TimeInterval.ATTR_NO_REGION_CENTER_TRANSFER) && rowIndex != 0) {
                        retValue = true;
                    }
                }
                break;
            case FREIGHT_TO_STATIONS:
                FreightNet freightNet = train.getDiagram().getFreightNet();
                if (rowIndex % 2 == 0 && (interval.isFreight() || interval.isFreightConnection())) {
                    StringBuilder result = new StringBuilder();
                    Map<Train, List<FreightDestination>> passedCargoDst = freightNet.getFreightPassedInNode(interval);
                    for (Map.Entry<Train, List<FreightDestination>> entry : passedCargoDst.entrySet()) {
                        List<FreightDestination> mList = entry.getValue();
                        result.append('(').append(diagram.getFreightNet().freightDstListToString(Locale.getDefault(),
                                interval.getOwnerAsNode(), mList));
                        result.append(" > ").append(entry.getKey().getDefaultName()).append(')');
                    }
                    if (interval.isFreightFrom()) {
                        List<FreightDestination> cargoDst = freightNet.getFreightToNodes(interval);
                        List<FreightDestination> mList = cargoDst;
                        if (!cargoDst.isEmpty() && result.length() > 0) {
                            result.append(' ');
                        }
                    result.append(diagram.getFreightNet().freightDstListToString(Locale.getDefault(),
                            interval.getOwnerAsNode(), mList));
                    }
                    retValue = result.toString();
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
                        if (time < start) {
                            time += TimeInterval.DAY;
                        }
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
                    boolean originalIsFreight = interval.isFreight();
                    train.changeStopTime(interval, time);
                    int firstRow = rowIndex - 1;
                    if (interval.isFreight() != originalIsFreight) {
                        firstRow = 0;
                    }
                    this.fireTableRowsUpdated(firstRow, lastRow);
                }
                break;
            case SPEED_LIMIT:
                // velocity limit
                Integer velocity = (Integer) aValue;
                if (velocity == null || velocity > 0) {
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
                    if (addedTime >= 0) {
                        train.changeSpeedAndAddedTime(interval, interval.getSpeedLimit(), addedTime);
                    }
                } else {
                    train.changeSpeedAndAddedTime(interval, interval.getSpeedLimit(), 0);
                }
                this.fireTableRowsUpdated(rowIndex, lastRow);
                break;
            case PLATFORM:
                // platform
                Track track = (Track) aValue;
                if (interval.isNodeOwner()) {
                    NodeTrack newTrack = (NodeTrack) track;
                    if (newTrack != null) {
                        train.changeNodeTrack(interval, newTrack);
                        this.fireTableRowsUpdated(rowIndex, rowIndex);
                    }
                } else if (interval.isLineOwner()) {
                    LineTrack newTrack = (LineTrack) track;
                    if (newTrack != null) {
                        train.changeLineTrack(interval, newTrack);
                        this.fireTableRowsUpdated(rowIndex, rowIndex);
                    }
                }
                break;
            case COMMENT:
                // comment
                String commentStr = ObjectsUtil.checkAndTrim((String) aValue);
                LocalizedString lComment = null;
                if (commentStr != null) {
                    lComment = LocalizedString.newBuilder(interval.getComment()).setDefaultString(commentStr).build();
                }
                interval.getAttributes().setRemove(TimeInterval.ATTR_COMMENT, lComment);
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
            case MANAGED_FREIGHT:
                interval.getAttributes().setBool(TimeInterval.ATTR_NOT_MANAGED_FREIGHT, !((Boolean) aValue));
                this.fireTableRowsUpdated(0, lastRow);
                break;
            case REGION_CENTER_TRANSFER:
                interval.setAttributeAsBool(TimeInterval.ATTR_NO_REGION_CENTER_TRANSFER, !((Boolean) aValue));
                this.fireTableRowsUpdated(0, lastRow);
                break;
            default:
                break;
        }
        editBlock = false;
    }

    public void setModel(ApplicationModel model) {
    }
}
