package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TranslatedString;

/**
 * One row of train timetable.
 *
 * @author jub
 */
@XmlType(propOrder={"station", "stationAbbr", "stationType", "track", "straight", "arrival", "departure",
    "speed", "setSpeed", "controlStation", "comment", "shunt", "occupied", "lineEnd", "lightSignals",
    "onControlled", "firstConcurrent", "concurrentTrains", "lineClass", "routePosition", "routePositionOut",
    "lineTracks", "freightDest"
})
public class TrainTimetableRow {

    private String station;
    private String stationAbbr;
    private String stationType;
    private String track;
    private Boolean straight;
    private String arrival;
    private String departure;
    private Integer speed;
    private Integer setSpeed;
    private Boolean controlStation;
    private LocalizedString comment;
    private Boolean shunt;
    private Boolean occupied;
    private Boolean lineEnd;
    private Boolean lightSignals;
    private Boolean onControlled;
    private Boolean firstConcurrent;
    private List<TranslatedString> concurrentTrains;
    private String lineClass;
    private Double routePosition;
    private Double routePositionOut;
    private Integer lineTracks;
    private List<FreightDstInfo> freightDest;

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

    public Integer getSetSpeed() {
        return setSpeed;
    }

    public void setSetSpeed(Integer trainSpeed) {
        this.setSpeed = trainSpeed;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getStationAbbr() {
        return stationAbbr;
    }

    public void setStationAbbr(String stationAbbr) {
        this.stationAbbr = stationAbbr;
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

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getComment() {
        return comment;
    }

    public void setComment(LocalizedString comment) {
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
    @XmlElementWrapper(name="concurrentTrains")
    @XmlJavaTypeAdapter(TStringAdapter.class)
    public List<TranslatedString> getConcurrentTrains() {
        return concurrentTrains;
    }

    public void setConcurrentTrains(List<TranslatedString> concurrentTrains) {
        this.concurrentTrains = concurrentTrains;
    }

    public String getLineClass() {
        return lineClass;
    }

    public void setLineClass(String lineClass) {
        this.lineClass = lineClass;
    }

    public Boolean getFirstConcurrent() {
        return firstConcurrent;
    }

    public void setFirstConcurrent(Boolean firstConcurrent) {
        this.firstConcurrent = firstConcurrent;
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

    public void setFreightDest(List<FreightDstInfo> freightDest) {
        this.freightDest = freightDest;
    }

    @XmlElement(name="dest")
    @XmlElementWrapper(name="freight")
    public List<FreightDstInfo> getFreightDest() {
        return freightDest;
    }
}
