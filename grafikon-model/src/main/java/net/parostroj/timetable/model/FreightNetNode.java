package net.parostroj.timetable.model;

/**
 * Freight net node (train with possible additional attributes).
 *
 * @author jub
 */
public class FreightNetNode implements AttributesHolder {

    private final Train train;
    private Attributes attributes;

    FreightNetNode(Train train) {
        this.train = train;
        this.setAttributes(new Attributes());
    }

    public Train getTrain() {
        return train;
    }

    @Override
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return this.attributes.remove(key);
    }

    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
}
