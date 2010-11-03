package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * One row of train timetable.
 *
 * @author jub
 */
@XmlType(propOrder={"station", "stationType", "track", "straight", "arrival", "departure",
    "speed", "controlStation", "comment", "shunt", "occupied", "lineEnd", "lightSignals",
    "onControlled", "trapezoidTrains", "lineClass", "routePosition", "routePositionOut",
    "lineTracks"
})
public class TrainTimetableRow {

    private String station;
    private String stationType;
    private String track;
    private Boolean straight;
    private String arrival;
    private String departure;
    private Integer speed;
    private Boolean controlStation;
    private String comment;
    private Boolean shunt;
    private Boolean occupied;
    private Boolean lineEnd;
    private Boolean lightSignals;
    private Boolean onControlled;
    private List<String> trapezoidTrains;
    private String lineClass;
    private Double routePosition;
    private Double routePositionOut;
    private Integer lineTracks;

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public Boolean getControlStation() {
        return controlStation;
    }

    public void setControlStation(Boolean controlStation) {
        this.controlStation = controlStation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(Boolean lineEnd) {
        this.lineEnd = lineEnd;
    }

    public Boolean getOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    public Boolean getShunt() {
        return shunt;
    }

    public void setShunt(Boolean shunt) {
        this.shunt = shunt;
    }

    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    public Boolean getLightSignals() {
        return lightSignals;
    }

    public void setLightSignals(Boolean lightSignals) {
        this.lightSignals = lightSignals;
    }

    public Boolean getOnControlled() {
        return onControlled;
    }

    public void setOnControlled(Boolean onControlled) {
        this.onControlled = onControlled;
    }

    public Boolean getStraight() {
        return straight;
    }

    public void setStraight(Boolean straight) {
        this.straight = straight;
    }

    @XmlElement(name="train")
    @XmlElementWrapper(name="trapezoidTrains")
    public List<String> getTrapezoidTrains() {
        return trapezoidTrains;
    }

    public void setTrapezoidTrains(List<String> trapezoidTrains) {
        this.trapezoidTrains = trapezoidTrains;
    }

    public String getLineClass() {
        return lineClass;
    }

    public void setLineClass(String lineClass) {
        this.lineClass = lineClass;
    }

    public Double getRoutePosition() {
        return routePosition;
    }

    public void setRoutePosition(Double routePosition) {
        this.routePosition = routePosition;
    }

    public Double getRoutePositionOut() {
        return routePositionOut;
    }

    public void setRoutePositionOut(Double routePositionOut) {
        this.routePositionOut = routePositionOut;
    }

    public Integer getLineTracks() {
        return lineTracks;
    }

    public void setLineTracks(Integer lineTracks) {
        this.lineTracks = lineTracks;
    }
}
