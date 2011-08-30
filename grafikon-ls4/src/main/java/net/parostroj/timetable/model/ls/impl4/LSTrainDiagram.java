package net.parostroj.timetable.model.ls.impl4;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.TrainDiagram;

/**
 * Storage for train diagram data.
 * 
 * @author jub
 */
@XmlRootElement(name = "train_diagram")
@XmlType(propOrder = {"id", "trainsData", "attributes", "changesTracking", "cycleTypes"})
public class LSTrainDiagram {
    
    private String id;
    private LSTrainsData trainsData;
    private LSAttributes attributes;
    private boolean changesTracking;
    private Set<LSTrainsCycleType> cycleTypes;
    
    public LSTrainDiagram() {
    }
    
    public LSTrainDiagram(TrainDiagram diagram) {
        id = diagram.getId();
        trainsData = new LSTrainsData(diagram.getTrainsData());
        attributes = new LSAttributes(diagram.getAttributes());
        changesTracking = diagram.getChangesTracker().isTrackingEnabled();
        for (String typeName : diagram.getCyclesTypes()) {
            LSTrainsCycleType lsType = new LSTrainsCycleType(diagram.getCyclesType(typeName));
            getCycleTypes().add(lsType);
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
        if (cycleTypes == null)
            cycleTypes = new HashSet<LSTrainsCycleType>();
        return cycleTypes;
    }
    
    public void setCycleTypes(Set<LSTrainsCycleType> cycleTypes) {
        this.cycleTypes = cycleTypes;
    }
}

