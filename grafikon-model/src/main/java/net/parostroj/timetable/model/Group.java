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
    private final AttributesWrapper attributesWrapper;

    Group(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributesWrapper = new AttributesWrapper(
                (attrs, change) -> diagram.fireEvent(new TrainDiagramEvent(diagram, change, Group.this)));
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
        return attributesWrapper.getAttributes().get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributesWrapper.getAttributes().set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributesWrapper.getAttributes().remove(key);
    }

    @Override
    public Attributes getAttributes() {
        return attributesWrapper.getAttributes();
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributesWrapper.setAttributes(attributes);
    }

    @Override
    public String toString() {
        return name;
    }
}
