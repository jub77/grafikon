package net.parostroj.timetable.model;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Region - for station attributes -> freight net support.
 *
 * @author jub
 */
public class Region implements Visitable, ObjectWithId, AttributesHolder, RegionAttributes {

    private final String id;
    private String name;
    private Attributes attributes;

    public Region(String id, String name) {
        this.id = id;
        this.name = name;
        this.setAttributes(new Attributes());
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
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
