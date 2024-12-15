package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Region;

/**
 * Storage for line class.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "attributes"})
public class LSRegion {

    private String id;
    // name kept only for backward compatibility
    private String name;
    private LSAttributes attributes;

    public LSRegion() {
    }

    public LSRegion(Region region) {
        this.id = region.getId();
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

    public DelayedAttributes<Region> createRegion(LSContext context) {
        Region region = context.getDiagram().getPartFactory().createRegion(id);
        // expected value -> null (for compatibility before version 4.18.2)
        region.setName(name);
        return new DelayedAttributes<>(region, attributes);
    }
}
