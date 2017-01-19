package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.Observable;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Line class
 *
 * @author jub
 */
public class LineClass implements AttributesHolder, ObjectWithId, Visitable, ItemListObject, Observable, LineClassAttributes {

    private final String id;
    private String name;
    private final Attributes attributes;

    private final ListenerSupport listenerSupport;

    public LineClass(String id, String name) {
        this.id = id;
        this.name = name;
        this.listenerSupport = new ListenerSupport();
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(LineClass.this, change)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_NAME, oldName, this.name)));
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void added() {
    }

    @Override
    public void removed() {
    }

    @Override
    public String toString() {
        return name;
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
