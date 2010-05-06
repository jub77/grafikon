package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.changes.DiagramChange;

/**
 * Storage for diagram change.
 *
 * @author jub
 */
@XmlType(propOrder={"type", "action", "objectId", "object"})
public class LSDiagramChange {

    private String type;
    private String action;
    private String objectId;
    private String object;

    public LSDiagramChange() {}

    public LSDiagramChange(DiagramChange change) {
        this.type = change.getType().name();
        this.action = change.getAction() != null ? change.getAction().name() : null;
        this.objectId = change.getObjectId();
        this.object = change.getObject();
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

    public DiagramChange createDiagramChange() {
        DiagramChange change = new DiagramChange();
        change.setType(DiagramChange.Type.valueOf(type));
        if (action != null) change.setAction(DiagramChange.Action.valueOf(action));
        change.setObjectId(objectId);
        change.setObject(object);
        return change;
    }
}
