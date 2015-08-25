package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributesListener;

class AttributesWrapper {

    private final AttributesListener listener;
    private Attributes attributes;

    public AttributesWrapper(AttributesListener listener) {
        this(listener, new Attributes());
    }

    public AttributesWrapper(AttributesListener listener, Attributes attributes) {
        this.listener = listener;
        this.setAttributes(attributes);
    }

    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && listener != null) {
            this.attributes.removeListener(listener);
        }
        this.attributes = attributes;
        this.attributes.addListener(listener);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public AttributesListener getListener() {
        return listener;
    }
}
