package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

public class Group implements ObjectWithId, Visitable, AttributesHolder, GroupAttributes, TrainDiagramPart {

    private final TrainDiagram diagram;
    /** ID. */
    private final String id;
    /** Name */
    private String name;
    /** Attributes. */
    private Attributes attributes;
    private AttributesListener attributesListener;

    Group(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.setAttributes(new Attributes());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this.diagram.fireEvent(new TrainDiagramEvent(diagram, new AttributeChange(ATTR_NAME, oldName, name), this));
        }
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = (attrs, change) -> diagram
                .fireEvent(new TrainDiagramEvent(diagram, change, Group.this));
        this.attributes.addListener(attributesListener);
    }

    @Override
    public String toString() {
        return name;
    }
}
