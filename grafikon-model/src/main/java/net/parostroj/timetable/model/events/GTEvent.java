package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Common predecessor for events.
 *
 * @author jub
 */
public abstract class GTEvent<T extends ObjectWithId> {

    private final T source;
    private final GTEventType type;

    private AttributeChange attributeChange;

    public GTEvent(T source, GTEventType type) {
        this.source = source;
        this.type = type;
    }

    public GTEvent(T source, AttributeChange change) {
        this(source, GTEventType.ATTRIBUTE);
        this.attributeChange = change;
    }

    public T getSource() {
        return source;
    }

    public GTEventType getType() {
        return type;
    }

    public AttributeChange getAttributeChange() {
        return attributeChange;
    }

    public void setAttributeChange(AttributeChange attributeChange) {
        this.attributeChange = attributeChange;
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    public abstract void accept(EventVisitor visitor);
}
