package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Train diagram event.
 * 
 * @author jub
 */
public class TrainDiagramEvent extends GTEvent<TrainDiagram> {

    private Route route;
    private Train train;
    private TrainType trainType;

    public TrainDiagramEvent(TrainDiagram diagram, GTEventType type) {
        super(diagram, type);
    }
    
    public TrainDiagramEvent(TrainDiagram diagram, GTEvent<?> event) {
        super(diagram, event);
    }

    public TrainDiagramEvent(TrainDiagram diagram, GTEventType type, Route route) {
        super(diagram, type);
        this.route = route;
    }

    public TrainDiagramEvent(TrainDiagram diagram, GTEventType type, Train train) {
        super(diagram, type);
        this.train = train;
    }

    public TrainDiagramEvent(TrainDiagram diagram, GTEventType type, TrainType trainType) {
        super(diagram, type);
        this.trainType = trainType;
    }

    public TrainDiagramEvent(TrainDiagram diagram, AttributeChange attributeChange) {
        super(diagram, GTEventType.ATTRIBUTE);
        setAttributeChange(attributeChange);
    }

    public Route getRoute() {
        return route;
    }

    public Train getTrain() {
        return train;
    }

    public TrainType getTrainType() {
        return trainType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainDiagramEvent[");
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
