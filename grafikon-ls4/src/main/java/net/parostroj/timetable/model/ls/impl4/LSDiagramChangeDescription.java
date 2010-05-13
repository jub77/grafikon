package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.changes.DiagramChangeDescription;

/**
 * Storage for diagram change description.
 *
 * @author jub
 */
@XmlType(propOrder={"desc", "params"})
public class LSDiagramChangeDescription {

    private String desc;
    private String[] params;

    public LSDiagramChangeDescription() {
    }

    public LSDiagramChangeDescription(DiagramChangeDescription description) {
        this.desc = description.getDescription();
        this.params = description.getParams();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @XmlElement(name="param")
    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public DiagramChangeDescription createDiagramChangeDescription() {
        DiagramChangeDescription result = new DiagramChangeDescription(desc, params);
        return result;
    }
}
