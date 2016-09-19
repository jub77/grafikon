package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Train timetable information.
 *
 * @author jub
 */
@XmlType(propOrder={"name", "completeName", "diesel", "electric", "categoryKey", "routeInfo",
    "engineDescription", "controlled", "weightData", "lengthData", "rows"})
public class TrainTimetable {

    private String name;
    private String completeName;
    private Boolean diesel;
    private Boolean electric;
    private String categoryKey;
    private List<RouteInfoPart> routeInfo;
    private String engineDescription;
    private Boolean controlled;
    private List<WeightDataRow> weightData;
    private LengthData lengthData;
    private List<TrainTimetableRow> rows;

    public TrainTimetable() {}

    public TrainTimetable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    @XmlElementWrapper
    @XmlElement(name="part")
    public List<RouteInfoPart> getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(List<RouteInfoPart> routeInfo) {
        this.routeInfo = routeInfo;
    }

    public String getEngineDescription() {
        return engineDescription;
    }

    public void setEngineDescription(String engineDescription) {
        this.engineDescription = engineDescription;
    }

    @XmlElementWrapper
    @XmlElement(name="row")
    public List<WeightDataRow> getWeightData() {
        return weightData;
    }

    public void setWeightData(List<WeightDataRow> weightData) {
        this.weightData = weightData;
    }

    public LengthData getLengthData() {
        return lengthData;
    }

    public void setLengthData(LengthData lengthData) {
        this.lengthData = lengthData;
    }

    @XmlElement(name="row")
    public List<TrainTimetableRow> getRows() {
        return rows;
    }

    public void setRows(List<TrainTimetableRow> rows) {
        this.rows = rows;
    }

    public Boolean getControlled() {
        return controlled;
    }

    public void setControlled(Boolean controlled) {
        this.controlled = controlled;
    }

    public Boolean getDiesel() {
        return diesel;
    }

    public void setDiesel(Boolean diesel) {
        this.diesel = diesel;
    }

    public Boolean getElectric() {
        return electric;
    }

    public void setElectric(Boolean electric) {
        this.electric = electric;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }
}
