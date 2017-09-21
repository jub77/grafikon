package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributesListener;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Freight net node (train with possible additional attributes).
 *
 * @author jub
 */
public class FNConnection extends Attributes implements ObjectWithId, Visitable, FNConnectionAttributes {

    private final TimeInterval from;
    private final TimeInterval to;

    FNConnection(TimeInterval from, TimeInterval to, AttributesListener listener) {
        this.from = from;
        this.to = to;
        if (listener != null) {
            this.addListener(listener);
        }
    }

    @Override
    public String getId() {
        return from.getId() + to.getId();
    }

    public TimeInterval getFrom() {
        return from;
    }

    public TimeInterval getTo() {
        return to;
    }

    public FreightConnectionFilter getFreightDstFilter(FreightConnectionFilter current, boolean ignoreFrom) {
        return FreightConnectionFilterFactory.createFilter(current, this, ignoreFrom);
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(from.getOwnerAsNode());
    }

    @Override
    public String toString() {
        return String.format("%s[%s]-%s[%s]", from.getTrain().getDefaultName(), from, to.getTrain().getDefaultName(), to);
    }
}
