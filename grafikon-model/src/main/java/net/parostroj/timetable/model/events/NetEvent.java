package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Net event.
 *
 * @author jub
 */
public class NetEvent extends GTEvent<Net> {

    private final Object object;
    private int fromIndex;
    private int toIndex;

    public NetEvent(Net net, GTEventType type, Object object) {
        super(net, type);
        this.object = object;
    }

    public NetEvent(Net net, GTEventType type, Object object, int fromIndex, int toIndex) {
        this(net, type, object);
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public Object getObject() {
        return object;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("NetEvent[");
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
