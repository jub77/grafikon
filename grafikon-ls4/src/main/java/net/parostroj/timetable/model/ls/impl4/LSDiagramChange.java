package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.changes.DiagramChange;

/**
 * Storage for diagram change.
 *
 * @author jub
 */
public class LSDiagramChange {

    private String type;
    private String subType;
    private String objectId;
    private String object;

    public LSDiagramChange() {}

    public LSDiagramChange(DiagramChange change) {
        this.type = change.getType().name();
        this.subType = change.getSubType() != null ? change.getSubType().name() : null;
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

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
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
        if (subType != null) change.setSubType(DiagramChange.SubType.valueOf(subType));
        change.setObjectId(objectId);
        change.setObject(object);
        return change;
    }
}
