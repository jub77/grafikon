package net.parostroj.timetable.model.events;

import java.util.Iterator;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Common predecessor for events.
 *
 * @author jub
 */
public abstract class GTEvent<T extends ObjectWithId> implements Iterable<GTEvent<?>>{

    private final T source;
    private final GTEvent<?> nestedEvent;
    private final GTEventType type;

    private AttributeChange attributeChange;

    public GTEvent(T source, GTEventType type) {
        this.source = source;
        this.nestedEvent = null;
        this.type = type;
    }

    public GTEvent(T source, GTEvent<?> nestedEvent) {
        this.source = source;
        this.nestedEvent = nestedEvent;
        this.type = GTEventType.NESTED;
    }

    public GTEvent(T source, AttributeChange change) {
        this(source, GTEventType.ATTRIBUTE);
        this.attributeChange = change;
    }

    public T getSource() {
        return source;
    }

    public GTEvent<?> getNestedEvent() {
        return nestedEvent;
    }

    public GTEventType getType() {
        return type;
    }

    public boolean isNested() {
        return nestedEvent != null;
    }

    /**
     * @return last nested event or this if there is no nested event present
     */
    public GTEvent<?> getLastNestedEvent() {
        if (!isNested())
            return this;
        else
            return getNestedEvent().getLastNestedEvent();
    }

    @Override
    public Iterator<GTEvent<?>> iterator() {
        return new Iterator<GTEvent<?>>() {

            private GTEvent<?> current = GTEvent.this;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public GTEvent<?> next() {
                GTEvent<?> event = current;
                current = event.getNestedEvent();
                return event;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
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
