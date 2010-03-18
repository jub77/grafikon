package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Net event.
 * 
 * @author jub
 */
public class NetEvent extends GTEvent<Net> {

    private Node node;
    private Line line;
    private LineClass lineClass;
    private int fromIndex;
    private int toIndex;

    public NetEvent(Net net, GTEventType type, Node node) {
        super(net, type);
    }

    public NetEvent(Net net, GTEventType type, Line line) {
        super(net, type);
        this.line = line;
    }

    public NetEvent(Net net, GTEventType type, LineClass lineClass) {
        super(net, type);
        this.lineClass = lineClass;
    }

    public NetEvent(Net net, GTEventType type, LineClass lineClass, int fromIndex, int toIndex) {
        super(net, type);
        this.lineClass = lineClass;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public NetEvent(Net net, GTEvent<?> event) {
        super(net, event);
    }

    public Line getLine() {
        return line;
    }

    public LineClass getLineClass() {
        return lineClass;
    }

    public Node getNode() {
        return node;
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
        if (getType() == GTEventType.NESTED) {
            builder.append(',').append(getNestedEvent());
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
