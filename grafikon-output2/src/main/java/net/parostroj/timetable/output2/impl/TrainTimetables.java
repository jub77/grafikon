package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Root element for train timetables.
 *
 * @author jub
 */
@XmlRootElement(name="trains")
@XmlType(propOrder={"routeLengthUnit", "routes", "routeNumbers", "routeStations", "validity", "cycle", "trainTimetables", "texts"})
public class TrainTimetables {

    private LengthUnit routeLengthUnit;
    private List<NetPartRouteInfo> routes;
    private String routeNumbers;
    private String routeStations;
    private String validity;
    private DriverCycle cycle;
    private List<TrainTimetable> trainTimetables;
    private List<Text> texts;

    public TrainTimetables() {
    }

    public TrainTimetables(List<TrainTimetable> trainTimetables) {
        this.trainTimetables = trainTimetables;
    }

    @XmlElement(name="train")
    public List<TrainTimetable> getTrainTimetables() {
        return trainTimetables;
    }

    public void setTrainTimetables(List<TrainTimetable> trainTimetables) {
        this.trainTimetables = trainTimetables;
    }

    @XmlElement(name="text")
    public List<Text> getTexts() {
        return texts;
    }

    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }

    @XmlJavaTypeAdapter(LengthUnitAdapter.class)
    public LengthUnit getRouteLengthUnit() {
        return routeLengthUnit;
    }

    public void setRouteLengthUnit(LengthUnit routeLengthUnit) {
        this.routeLengthUnit = routeLengthUnit;
    }

    @XmlElement(name="route")
    public List<NetPartRouteInfo> getRoutes() {
        if (routes == null)
            routes = new LinkedList<>();
        return routes;
    }

    public void setRoutes(List<NetPartRouteInfo> routes) {
        this.routes = routes;
    }

    public String getRouteNumbers() {
        return routeNumbers;
    }

    public void setRouteNumbers(String routeNumbers) {
        this.routeNumbers = routeNumbers;
    }

    public String getRouteStations() {
        return routeStations;
    }

    public void setRouteStations(String routeStations) {
        this.routeStations = routeStations;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public DriverCycle getCycle() {
        return cycle;
    }

    public void setCycle(DriverCycle cycle) {
        this.cycle = cycle;
    }
}
