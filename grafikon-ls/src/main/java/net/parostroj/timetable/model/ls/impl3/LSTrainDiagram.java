package net.parostroj.timetable.model.ls.impl3;

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
@XmlType(propOrder = {"id", "trainsData", "attributes"})
public class LSTrainDiagram {
    
    private String id;
    private LSTrainsData trainsData;
    private LSAttributes attributes;
    
    public LSTrainDiagram() {
    }
    
    public LSTrainDiagram(TrainDiagram diagram) {
        id = diagram.getId();
        trainsData = new LSTrainsData(diagram.getTrainsData());
        attributes = new LSAttributes(diagram.getAttributes());
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
}
