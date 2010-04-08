package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlType;

/**
 * Row for station timetable.
 *
 * @author jub
 */
@XmlType(propOrder = {"trainName", "from", "arrival", "to", "departure", "end", "track", "technologicalTime", "occupied", "comment",
    "engineTo", "engineFrom", "trainUnitTo", "trainUnitFrom", "length"})
public class StationTimetableRow {

    private String trainName;
    private String from;
    private String arrival;
    private String to;
    private String departure;
    private String end;
    private String track;
    private boolean technologicalTime;
    private boolean occupied;
    private String comment;
    private List<EngineTo> engineTo;
    private List<EngineFrom> engineFrom;
    private List<TrainUnitTo> trainUnitTo;
    private List<TrainUnitFrom> trainUnitFrom;
    private LengthInfo length;

    public StationTimetableRow() {
    }

    public StationTimetableRow(String trainName, String from, String fromTime, String to, String toTime, String end, String track) {
        this.trainName = trainName;
        this.from = from;
        this.arrival = fromTime;
        this.to = to;
        this.end = end;
        this.departure = toTime;
        this.track = track;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public boolean isTechnologicalTime() {
        return technologicalTime;
    }

    public void setTechnologicalTime(boolean technologicalTime) {
        this.technologicalTime = technologicalTime;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<EngineFrom> getEngineFrom() {
        if (engineFrom == null) {
            engineFrom = new LinkedList<EngineFrom>();
        }
        return engineFrom;
    }

    public void setEngineFrom(List<EngineFrom> engineFrom) {
        this.engineFrom = engineFrom;
    }

    public List<EngineTo> getEngineTo() {
        if (engineTo == null) {
            engineTo = new LinkedList<EngineTo>();
        }
        return engineTo;
    }

    public void setEngineTo(List<EngineTo> engineTo) {
        this.engineTo = engineTo;
    }

    public List<TrainUnitFrom> getTrainUnitFrom() {
        if (trainUnitFrom == null) {
            trainUnitFrom = new LinkedList<TrainUnitFrom>();
        }
        return trainUnitFrom;
    }

    public void setTrainUnitFrom(List<TrainUnitFrom> trainUnitFrom) {
        this.trainUnitFrom = trainUnitFrom;
    }

    public List<TrainUnitTo> getTrainUnitTo() {
        if (trainUnitTo == null) {
            trainUnitTo = new LinkedList<TrainUnitTo>();
        }
        return trainUnitTo;
    }

    public void setTrainUnitTo(List<TrainUnitTo> trainUnitTo) {
        this.trainUnitTo = trainUnitTo;
    }

    public LengthInfo getLength() {
        return length;
    }

    public void setLength(LengthInfo length) {
        this.length = length;
    }
}
