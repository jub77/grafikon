package net.parostroj.timetable.model.ls.impl4;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;

/**
 * Storage for train diagram data.
 *
 * @author jub
 */
@XmlRootElement(name = "train_diagram")
@XmlType(propOrder = {"id", "trainsData", "attributes", "changesTracking", "cycleTypes", "groups", "companies"})
public class LSTrainDiagram {

    private String id;
    private LSTrainsData trainsData;
    private LSAttributes attributes;
    private boolean changesTracking;
    private Set<LSTrainsCycleType> cycleTypes;
    private List<LSGroup> groups;
    private List<LSCompany> companies;

    public LSTrainDiagram() {
    }

    public LSTrainDiagram(TrainDiagram diagram) {
        id = diagram.getId();
        trainsData = new LSTrainsData(diagram.getTrainsData());
        attributes = new LSAttributes(diagram.getAttributes());
        changesTracking = diagram.getChangesTracker().isTrackingEnabled();
        for (TrainsCycleType type : diagram.getCycleTypes()) {
            LSTrainsCycleType lsType = new LSTrainsCycleType(type);
            getCycleTypes().add(lsType);
        }
        for (Group group : diagram.getGroups()) {
            LSGroup lsGroup = new LSGroup(group);
            getGroups().add(lsGroup);
        }
        for (Company company : diagram.getCompanies()) {
            LSCompany lsCompany = new LSCompany(company);
            getCompanies().add(lsCompany);
        }
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "trains_data")
    public LSTrainsData getTrainsData() {
        return trainsData;
    }

    public void setTrainsData(LSTrainsData trainsData) {
        this.trainsData = trainsData;
    }

    public boolean isChangesTracking() {
        return changesTracking;
    }

    public void setChangesTracking(boolean changesTracking) {
        this.changesTracking = changesTracking;
    }


    @XmlElement(name = "cycle_type")
    public Set<LSTrainsCycleType> getCycleTypes() {
        if (cycleTypes == null) {
            cycleTypes = new HashSet<>();
        }
        return cycleTypes;
    }

    public void setCycleTypes(Set<LSTrainsCycleType> cycleTypes) {
        this.cycleTypes = cycleTypes;
    }

    @XmlElement(name = "group")
    public List<LSGroup> getGroups() {
        if (groups == null) {
            groups = new LinkedList<>();
        }
        return groups;
    }

    public void setGroups(List<LSGroup> groups) {
        this.groups = groups;
    }

    @XmlElement(name = "company")
    public List<LSCompany> getCompanies() {
        if (companies == null) {
            companies = new LinkedList<>();
        }
        return companies;
    }

    public void setCompanies(List<LSCompany> companies) {
        this.companies = companies;
    }
}
