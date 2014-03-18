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
    "engine", "trainUnit", "cycle", "length"})
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
    private List<CycleFromTo> engine;
    private List<CycleFromTo> trainUnit;
    private List<CycleWithTypeFromTo> cycle;

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

    public List<CycleFromTo> getEngine() {
        if (engine == null) {
            engine = new LinkedList<CycleFromTo>();
        }
        return engine;
    }

    public void setEngine(List<CycleFromTo> engine) {
        this.engine = engine;
    }

    public List<CycleFromTo> getTrainUnit() {
        if (trainUnit == null) {
            trainUnit = new LinkedList<CycleFromTo>();
        }
        return trainUnit;
    }

    public void setTrainUnit(List<CycleFromTo> trainUnit) {
        this.trainUnit = trainUnit;
    }

    public List<CycleWithTypeFromTo> getCycle() {
        if (cycle == null)
            cycle = new LinkedList<CycleWithTypeFromTo>();
        return cycle;
    }

    public void setCycle(List<CycleWithTypeFromTo> cycle) {
        this.cycle = cycle;
    }

    public LengthInfo getLength() {
        return length;
    }

    public void setLength(LengthInfo length) {
        this.length = length;
    }
}
