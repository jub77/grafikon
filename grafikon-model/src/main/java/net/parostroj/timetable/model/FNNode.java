package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributesListener;
import net.parostroj.timetable.utils.Conversions;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Freight net node (train with possible additional attributes).
 *
 * @author jub
 */
public class FNNode extends Attributes implements ObjectWithId, Visitable {

    private final Train train;
    private Location location;

    FNNode(Train train, AttributesListener listener) {
        this.train = train;
        this.addListener(listener);
        this.location = new Location(0, 0);
    }

    public Train getTrain() {
        return train;
    }

    public void setLocation(Location location) {
        if (!Conversions.compareWithNull(location, this.location)) {
            this.location = location;
        }
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String getId() {
        return train.getId();
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(train);
    }

    @Override
    public String toString() {
        return String.format("%s%n%s-%s", train.getName(), train.getStartNode().getAbbr(),
                train.getEndNode().getAbbr());
    }
}