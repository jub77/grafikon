package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Region - for station attributes -> freight net support.
 *
 * @author jub
 */
public class Region implements Visitable, ObjectWithId, AttributesHolder, RegionAttributes, TrainDiagramPart, ItemListObject {

    private final TrainDiagram diagram;
    private final String id;
    private final Attributes attributes;

    private boolean events;

    Region(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes((attrs, change) -> {if (events) diagram.fireEvent(new Event(diagram, Region.this, change)); });
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public String getName() {
        return attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        attributes.set(ATTR_NAME, name);
    }

    @Override
    public void added() {
        events = true;
    }

    @Override
    public void removed() {
        events = false;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
