package net.parostroj.timetable.model.ls.impl4;

import java.util.Calendar;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.changes.DiagramChangeDescription;

/**
 * Storage for diagram change description.
 *
 * @author jub
 */
@XmlType(propOrder={"date", "desc", "params"})
public class LSDiagramChangeDescription {

    private String desc;
    private String[] params;
    private Calendar date;

    public LSDiagramChangeDescription() {
    }

    public LSDiagramChangeDescription(DiagramChangeDescription description) {
        this.desc = description.getDescription();
        this.params = description.getParams();
        this.date = description.getDate();
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
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
        result.setDate(date);
        return result;
    }
}
