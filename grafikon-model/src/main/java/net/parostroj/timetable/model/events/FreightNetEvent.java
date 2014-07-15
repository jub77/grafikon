package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Freight net event.
 *
 * @author jub
 */
public class FreightNetEvent extends GTEvent<FreightNet> {

    private final FNConnection connection;

    public FreightNetEvent(FreightNet net, AttributeChange attributeChange) {
        super(net, attributeChange);
        this.connection = null;
    }

    public FreightNetEvent(FreightNet net, GTEventType type, AttributeChange attributeChange, FNConnection connection) {
        super(net, type);
        setAttributeChange(attributeChange);
        this.connection = connection;
    }

    public FreightNetEvent(FreightNet net, GTEventType type, FNConnection connection) {
        super(net, type);
        this.connection = connection;
    }

    public FNConnection getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getType() != GTEventType.FREIGHT_NET_CONNECTION_ATTRIBUTE ? "FreightNetEvent[" : "FreightNetEvent(connection)[");
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
