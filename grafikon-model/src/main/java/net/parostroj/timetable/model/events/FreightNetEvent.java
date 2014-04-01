package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.FNNode;
import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Freight net event.
 *
 * @author jub
 */
public class FreightNetEvent extends GTEvent<FreightNet> {

    private final FNNode node;
    private final FNConnection connection;

    public FreightNetEvent(FreightNet net, AttributeChange attributeChange) {
        super(net, attributeChange);
        this.node = null;
        this.connection = null;
    }

    public FreightNetEvent(FreightNet net, GTEventType type, AttributeChange attributeChange, FNNode node, FNConnection connection) {
        super(net, type);
        setAttributeChange(attributeChange);
        this.node = node;
        this.connection = connection;
    }

    public FreightNetEvent(FreightNet net, GTEventType type, FNNode node, FNConnection connection) {
        super(net, type);
        this.node = node;
        this.connection = connection;
    }

    public FNConnection getConnection() {
        return connection;
    }

    public FNNode getNode() {
        return node;
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
