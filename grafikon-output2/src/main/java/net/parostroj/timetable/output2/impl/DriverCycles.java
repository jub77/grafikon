package net.parostroj.timetable.output2.impl;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Driver cycles.
 *
 * @author jub
 */
@XmlRootElement
@XmlType(propOrder={"routeNumbers", "routeStations", "validity", "cycles"})
public class DriverCycles {

    private String routeNumbers;
    private String routeStations;
    private String validity;
    private List<DriverCycle> cycles;

    public DriverCycles() {
    }

    public DriverCycles(List<DriverCycle> cycles) {
        this.cycles = cycles;
    }

    @XmlElement(name="cycle")
    public List<DriverCycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<DriverCycle> cycles) {
        this.cycles = cycles;
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
}
