package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.TextItemEvent;
import net.parostroj.timetable.model.events.TextItemListener;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Text item.
 *
 * @author jub
 */
public class TextItem implements ObjectWithId, AttributesHolder, Visitable {

    private final String id;
    private final TrainDiagram diagram;

    private String text;
    private String name;
    private String type;
    private Attributes attributes;
    private GTListenerSupport<TextItemListener, TextItemEvent> listenerSupport;

    public TextItem(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes();
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange("text", oldText, text)));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        String oldType = this.type;
        this.type = type;
        this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange("type", oldType, type)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.listenerSupport.fireEvent(new TextItemEvent(this, new AttributeChange("name", oldName, name)));
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        Object oldValue = attributes.get(key);
        attributes.put(key, value);
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
        this.attributes = attributes;
    }

    /**
     * adds listener to train.
     * @param listener listener
     */
    public void addListener(TextItemListener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener from train.
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
