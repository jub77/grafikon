package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Output;

/**
 * Storage for outputs.
 *
 * @author jub
 */
@XmlRootElement(name = "output")
@XmlType(name = "output", propOrder = {"id", "attributes"})
public class LSOutput {

    private String id;
    private LSAttributes attributes;

    public LSOutput() {
    }

    public LSOutput(Output output) {
        this.id = output.getId();
        this.attributes = new LSAttributes(output.getAttributes());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public Output createOutput(LSContext context) {
        Output output = context.getDiagram().getPartFactory().createOutput(id);
        output.getAttributes().add(attributes.createAttributes(context));
        return output;
    }
}
