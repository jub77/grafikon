package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Region - for station attributes -> freight net support.
 *
 * @author jub
 */
public class Region implements Visitable, ObjectWithId, AttributesHolder, RegionAttributes, TrainDiagramPart {

    private final TrainDiagram diagram;
    private final String id;
    private final Attributes attributes;

    Region(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes((attrs, change) -> diagram.fireEvent(new TrainDiagramEvent(diagram, change, Region.this)));
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

    public String getName() {
        return attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        attributes.set(ATTR_NAME, name);
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
