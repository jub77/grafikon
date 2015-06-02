package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for line class.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "attributes"})
public class LSRegion {

    private String id;
    private String name;
    private LSAttributes attributes;

    public LSRegion() {
    }

    public LSRegion(Region region) {
        this.id = region.getId();
        this.name = region.getName();
        this.attributes = new LSAttributes(region.getAttributes());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public Region createRegion(TrainDiagram diagram) throws LSException {
        Region region = diagram.createRegion(id, name);
        region.setAttributes(attributes.createAttributes(diagram));
        return region;
    }
}
