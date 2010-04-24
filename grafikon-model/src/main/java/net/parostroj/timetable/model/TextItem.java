package net.parostroj.timetable.model;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Text item.
 *
 * @author jub
 */
public class TextItem implements ObjectWithId, AttributesHolder {

    private final String id;
    private final TrainDiagram diagram;

    private String text;
    private String name;
    private String type;
    private Attributes attributes;

    public TextItem(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes();
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
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
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
}
