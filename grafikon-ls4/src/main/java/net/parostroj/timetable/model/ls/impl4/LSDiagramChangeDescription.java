package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.changes.DiagramChangeDescription;
import net.parostroj.timetable.model.changes.Parameter;

/**
 * Storage for diagram change description.
 *
 * @author jub
 */
@XmlType(propOrder={"desc", "params"})
public class LSDiagramChangeDescription {

    private String desc;
    private LSParameter[] params;

    public LSDiagramChangeDescription() {
    }

    public LSDiagramChangeDescription(DiagramChangeDescription description) {
        this.desc = description.getDescription();
        if (description.getParams() != null) {
            this.params = new LSParameter[description.getParams().length];
            for (int i = 0; i < description.getParams().length; i++) {
                this.params[i] = new LSParameter(description.getParams()[i]);
            }
        }
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @XmlElement(name="param")
    public LSParameter[] getParams() {
        return params;
    }

    public void setParams(LSParameter[] params) {
        this.params = params;
    }

    public DiagramChangeDescription createDiagramChangeDescription() {
        DiagramChangeDescription result = new DiagramChangeDescription(desc);
        if (this.params != null) {
            Parameter[] created = new Parameter[this.params.length];
            for (int i = 0; i < this.params.length; i++) {
                created[i] = this.params[i].createParameter();
            }
            result.setParams(created);
        }
        return result;
    }
}
