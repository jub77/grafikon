package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.ObservableObject;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Line class
 *
 * @author jub
 */
public class LineClass implements AttributesHolder, ObjectWithId, Visitable, ItemCollectionObject, ObservableObject {

    public static final String ATTR_NAME = "name";

    private final String id;
    private final Attributes attributes;

    private final ListenerSupport listenerSupport;

    public LineClass(String id) {
        this.id = id;
        this.listenerSupport = new ListenerSupport();
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(LineClass.this, change)));
    }

    public String getName() {
        return this.attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        this.attributes.setRemove(ATTR_NAME, name);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void added() {
        // no action
    }

    @Override
    public void removed() {
        // not action
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }
}
