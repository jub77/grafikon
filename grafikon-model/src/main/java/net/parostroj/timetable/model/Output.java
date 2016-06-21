package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.ListenerSupport;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Output based on OutputTemplate - separate settings.
 *
 * @author jub
 */
public class Output implements ObjectWithId, AttributesHolder, OutputAttributes, Observable, Visitable {

    private final String id;

    private final Attributes attributes;
    private final ListenerSupport listenerSupport;

    Output(String id) {
        this.id = id;
        this.listenerSupport = new ListenerSupport();
        this.attributes = new Attributes((attrs, change) -> listenerSupport.fireEvent(new Event(Output.this, change)));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public OutputTemplate getTemplate() {
        return attributes.get(ATTR_TEMPLATE, OutputTemplate.class);
    }

    public void setTemplate(OutputTemplate template) {
        attributes.setRemove(ATTR_TEMPLATE, template);
    }

    public LocalizedString getName() {
        return attributes.get(ATTR_NAME, LocalizedString.class);
    }

    public void setName(LocalizedString name) {
        attributes.setRemove(ATTR_NAME, name);
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
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getName() != null ? getName().getDefaultString() : "<none>";
    }
}
