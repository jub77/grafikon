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
public class TextItem implements ObjectWithId, AttributesHolder, Visitable, TextItemAttributes, Observable {

    public enum Type {
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

        @Override
        public String toString() {
            return key;
        }
    }

    private final String id;
    private final TrainDiagram diagram;

    private String name;
    private Type type;
    private TextTemplate template;
    private final Attributes attributes;
    private final ListenerSupport listenerSupport;

    public TextItem(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new ListenerSupport();
        attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(TextItem.this, change)));
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
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TYPE, oldType, type)));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public TextTemplate getTemplate() {
        return template;
    }

    public void setTemplate(TextTemplate template) {
        if (!ObjectsUtil.compareWithNull(template, this.template)) {
            TextTemplate oldTemplate = this.template;
            this.template = template;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TEMPLATE, oldTemplate, template)));
        }
    }

    public String getText() {
        return template != null ? template.evaluate(TextTemplate.getBinding("diagram", diagram)) : "";
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * adds listener.
     *
     * @param listener listener
     */
    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener.
     *
     * @param listener listener
     */
    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    public void removeAllListeners() {
        listenerSupport.removeAllListeners();
    }

    @Override
    public String toString() {
        return name + "(" + type + ")";
    }
}
