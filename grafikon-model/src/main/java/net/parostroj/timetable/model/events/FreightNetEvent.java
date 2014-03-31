package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Freight net event.
 *
 * @author jub
 */
public class FreightNetEvent extends GTEvent<FreightNet> {

    private final Object object;

    public FreightNetEvent(FreightNet net, GTEventType type, Object object) {
        super(net, type);
        this.object = object;
    }

    public FreightNetEvent(FreightNet net, AttributeChange attributeChange, Object object) {
        this(net, GTEventType.ATTRIBUTE, object);
        setAttributeChange(attributeChange);
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("FreightNetEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
