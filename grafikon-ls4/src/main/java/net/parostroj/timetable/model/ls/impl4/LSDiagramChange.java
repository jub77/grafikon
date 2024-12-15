package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.changes.DiagramChange;
import net.parostroj.timetable.model.changes.DiagramChangeDescription;

/**
 * Storage for diagram change.
 *
 * @author jub
 */
@XmlType(propOrder={"type", "action", "objectId", "object", "descs"})
public class LSDiagramChange {

    private String type;
    private String action;
    private String objectId;
    private String object;
    private List<LSDiagramChangeDescription> descs;

    public LSDiagramChange() {}

    public LSDiagramChange(DiagramChange change) {
        this.type = change.getType().name();
        this.action = change.getAction() != null ? change.getAction().name() : null;
        this.objectId = change.getObjectId();
        this.object = change.getObject();
        if (change.getDescriptions() != null) {
            this.descs = new LinkedList<>();
            for (DiagramChangeDescription d : change.getDescriptions()) {
                this.descs.add(new LSDiagramChangeDescription(d));
            }
        }
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LSDiagramChangeDescription> getDescs() {
        return descs;
    }

    @XmlElement(name="desc")
    public void setDescs(List<LSDiagramChangeDescription> descs) {
        this.descs = descs;
    }

    public DiagramChange createDiagramChange() {
        DiagramChange change = new DiagramChange();
        change.setType(DiagramChange.Type.valueOf(type));
        if (action != null) change.setAction(DiagramChange.Action.valueOf(action));
        change.setObjectId(objectId);
        change.setObject(object);
        if (this.descs != null) {
            List<DiagramChangeDescription> ds = new LinkedList<>();
            for (LSDiagramChangeDescription d : this.descs) {
                ds.add(d.createDiagramChangeDescription());
            }
            change.setDescriptions(ds);
        }
        return change;
    }
}
