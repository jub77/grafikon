package net.parostroj.timetable.model.events;

/**
 * Event.
 *
 * @author jub
 */
public class Event {

    public enum Type {
        ATTRIBUTE, ADDED(true), REMOVED(true), MOVED(true), REPLACED(true), OBJECT_ATTRIBUTE, SPECIAL;

        private final boolean list;

        private Type(boolean list) {
            this.list = list;
        }

        private Type() {
            this(false);
        }

        public boolean isList() {
            return list;
        }
    }

    private final Type type;
    private final Object source;
    private final AttributeChange attributeChange;
    private final Object object;
    private final Object data;

    public Event(Object source, AttributeChange attributeChange) {
        this.source = source;
        this.attributeChange = attributeChange;
        this.object = null;
        this.type = Type.ATTRIBUTE;
        this.data = null;
    }

    public Event(Object source, Object object, AttributeChange attributeChange) {
        this.source = source;
        this.attributeChange = attributeChange;
        this.object = object;
        this.type = Type.OBJECT_ATTRIBUTE;
        this.data = null;
    }

    public Event(Object source, Type type, Object object) {
        this(source, type, object, null);
    }

    public Event(Object source, Type type, Object object, Object data) {
        this.source = source;
        this.attributeChange = null;
        this.object = object;
        this.type = type;
        this.data = data;
    }

    public Event(Object source, Object data) {
        this.source = source;
        this.attributeChange = null;
        this.type = Type.SPECIAL;
        this.object = null;
        this.data = data;
    }

    public Object getSource() {
        return source;
    }

    public Object getObject() {
        return object;
    }

    public AttributeChange getAttributeChange() {
        return attributeChange;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
