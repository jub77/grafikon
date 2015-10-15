package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Output template.
 *
 * @author jub
 */
public class OutputTemplate implements ObjectWithId, Visitable, AttributesHolder, OutputTemplateAttributes, TrainDiagramPart, Observable {

    public static final String DEFAULT_OUTPUT = "groovy";

    private final String id;
    private final TrainDiagram diagram;

    private String name;
    private TextTemplate template;
    private Script script;

    private final ItemSet<Attachment> attachments;

    private final Attributes attributes;
    private final ListenerSupport listenerSupport;

    public OutputTemplate(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new ListenerSupport();
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(OutputTemplate.this, change)));
        this.attachments = new ItemSetImpl<Attachment>((type, item) -> {
            AttributeChange change = null;
            switch (type) {
                case ADDED:
                    change = new AttributeChange(ATTR_ATTACHMENT, null, item);
                    break;
                case REMOVED:
                    change = new AttributeChange(ATTR_ATTACHMENT, item, null);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + type);
            }
            listenerSupport.fireEvent(new Event(OutputTemplate.this, change));
        });
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public void setName(String name) {
        if (!ObjectsUtil.compareWithNull(name, this.name)) {
            String oldName = this.name;
            this.name = name;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public String getName() {
        return name;
    }

    public void setTemplate(TextTemplate template) {
        if (!ObjectsUtil.compareWithNull(template, this.template)) {
            TextTemplate oldTemplate = this.template;
            this.template = template;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_TEMPLATE, oldTemplate, template)));
        }
    }

    public TextTemplate getTemplate() {
        return template;
    }

    public void setScript(Script script) {
        if (!ObjectsUtil.compareWithNull(script, this.script)) {
            Script oldScript = this.script;
            this.script = script;
            this.listenerSupport.fireEvent(new Event(this, new AttributeChange(ATTR_SCRIPT, oldScript, script)));
        }
    }

    public Script getScript() {
        return script;
    }

    public ItemSet<Attachment> getAttachments() {
        return attachments;
    }

    public String getOutput() {
        String output = this.getAttribute(ATTR_OUTPUT, String.class);
        return output != null ? output : DEFAULT_OUTPUT;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
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
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener.
     *
     * @param listener listener
     */
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    public void removeAllListeners() {
        listenerSupport.removeAllListeners();
    }

    @Override
    public String toString() {
        return name;
    }
}
