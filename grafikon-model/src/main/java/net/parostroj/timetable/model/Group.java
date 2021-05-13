package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

public class Group implements ObjectWithId, Visitable, AttributesHolder, TrainDiagramPart, ItemCollectionObject {

    public static final String ATTR_NAME = "name";

    private final TrainDiagram diagram;
    /** ID. */
    private final String id;
    /** Attributes. */
    private final Attributes attributes;

    private boolean events;

    Group(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.events = false;
        this.attributes = new Attributes(
                (attrs, change) -> { if (events) diagram.fireEvent(new Event(diagram, Group.this, change)); });
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
        return attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        attributes.setRemove(ATTR_NAME, name);
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public void added() {
        this.events = true;
    }

    @Override
    public void removed() {
        this.events = false;
    }

    @Override
    public String toString() {
        return getName();
    }
}
