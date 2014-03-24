package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for output templates.
 *
 * @author jub
 */
@XmlRootElement(name = "output_template")
@XmlType(propOrder = {"id", "name", "template", "script", "attributes"})
public class LSOutputTemplate {

    private String id;
    private String name;
    private LSTextTemplate template;
    private LSScript script;
    private LSAttributes attributes;

    public LSOutputTemplate() {
    }

    public LSOutputTemplate(OutputTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.template = new LSTextTemplate(template.getTemplate());
        this.attributes = new LSAttributes(template.getAttributes());
        this.script = new LSScript(template.getScript());
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

    public LSTextTemplate getTemplate() {
        return template;
    }

    public void setTemplate(LSTextTemplate template) {
        this.template = template;
    }

    public LSScript getScript() {
        return script;
    }

    public void setScript(LSScript script) {
        this.script = script;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public OutputTemplate createOutputTemplate(TrainDiagram diagram) throws LSException {
        OutputTemplate outputTemplate = new OutputTemplate(id, diagram);
        outputTemplate.setName(name);
        if (this.template != null) {
            outputTemplate.setTemplate(this.template.createTextTemplate());
        }
        if (this.script != null) {
            outputTemplate.setScript(this.script.createScript());
        }
        outputTemplate.setAttributes(attributes.createAttributes(diagram));
        return outputTemplate;
    }
}
