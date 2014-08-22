package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Text item.
 *
 * @author jub
 */
public class TextItem implements ObjectWithId, AttributesHolder, Visitable, TextItemAttributes {

    public static enum Type {
        PLAIN_TEXT("plain");

        private String key;

        private Type(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static Type fromKey(String key) {
            for (Type type : values()) {
                if (type.getKey().equals(key)) {
                    return type;
                }
            }
            return PLAIN_TEXT;
        }
    }

    private final String id;
    private final TrainDiagram diagram;

    private String name;
    private Type type;
    private TextTemplate template;
    private Attributes attributes;
    private final GTListenerSupport<TextItemListener, TextItemEvent> listenerSupport;
    private AttributesListener attributesListener;

    public TextItem(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.setAttributes(new Attributes());
        listenerSupport = new GTListenerSupport<TextItemListener, TextItemEvent>(new GTEventSender<TextItemListener, TextItemEvent>() {

            @Override
            public void fireEvent(TextItemListener listener, TextItemEvent event) {
                listener.textItemChanged(event);
            }
        });
    }

    @Override
    public String getId() {
        return id;
    }

    public TrainDiagram getDiagram() {
        return diagram;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type != this.type) {
            Type oldType = this.type;
            this.type = type;
            this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange(ATTR_TYPE, oldType, type)));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public TextTemplate getTemplate() {
        return template;
    }

    public void setTemplate(TextTemplate template) {
        if (!ObjectsUtil.compareWithNull(template, this.template)) {
            TextTemplate oldTemplate = this.template;
            this.template = template;
            this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange(ATTR_TEMPLATE, oldTemplate, template)));
        }
    }

    public String getText() {
        return template != null ? template.evaluate(diagram) : "";
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        Object oldValue = attributes.get(key);
        attributes.set(key, value);
        this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange(key, oldValue, value)));
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                listenerSupport.fireEvent(new TextItemEvent(TextItem.this, change));
            }
        };
        this.attributes.addListener(attributesListener);
    }

    /**
     * adds listener.
     *
     * @param listener listener
     */
    public void addListener(TextItemListener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener.
     *
     * @param listener listener
     */
    public void removeListener(TextItemListener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public String toString() {
        return name + "(" + type + ")";
    }
}
