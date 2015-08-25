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
public class OutputTemplate implements ObjectWithId, Visitable, AttributesHolder, OutputTemplateAttributes, TrainDiagramPart {

    public static final String DEFAULT_OUTPUT = "groovy";

    private final String id;
    private final TrainDiagram diagram;

    private String name;
    private TextTemplate template;
    private Script script;

    private final ItemList<Attachment> attachments;

    private final AttributesWrapper attributesWrapper;
    private final GTListenerSupport<OutputTemplateListener, OutputTemplateEvent> listenerSupport;

    public OutputTemplate(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new GTListenerSupport<OutputTemplateListener, OutputTemplateEvent>(
                (listener, event) -> listener.outputTemplateChanged(event));
        this.attributesWrapper = new AttributesWrapper(
                (attrs, change) -> listenerSupport.fireEvent(new OutputTemplateEvent(OutputTemplate.this, change)));
        this.attachments = new ItemList<Attachment>(GTEventType.ATTRIBUTE, GTEventType.ATTRIBUTE) {
            @Override
            protected void fireEvent(Type type, GTEventType eventType, Attachment item, int newIndex, int oldIndex) {
                AttributeChange change = null;
                switch (type) {
                    case ADD:
                        change = new AttributeChange(ATTR_ATTACHMENT, null, item);
                        break;
                    case REMOVE:
                        change = new AttributeChange(ATTR_ATTACHMENT, item, null);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported type: " + type);
                }
                listenerSupport.fireEvent(new OutputTemplateEvent(OutputTemplate.this, change));
            }
        };
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
            this.listenerSupport.fireEvent(new OutputTemplateEvent(this, new AttributeChange(ATTR_NAME, oldName, name)));
        }
    }

    public String getName() {
        return name;
    }

    public void setTemplate(TextTemplate template) {
        if (!ObjectsUtil.compareWithNull(template, this.template)) {
            TextTemplate oldTemplate = this.template;
            this.template = template;
            this.listenerSupport.fireEvent(new OutputTemplateEvent(this, new AttributeChange(ATTR_TEMPLATE, oldTemplate, template)));
        }
    }

    public TextTemplate getTemplate() {
        return template;
    }

    public void setScript(Script script) {
        if (!ObjectsUtil.compareWithNull(script, this.script)) {
            Script oldScript = this.script;
            this.script = script;
            this.listenerSupport.fireEvent(new OutputTemplateEvent(this, new AttributeChange(ATTR_SCRIPT, oldScript, script)));
        }
    }

    public Script getScript() {
        return script;
    }

    public ItemList<Attachment> getAttachments() {
        return attachments;
    }

    public String getOutput() {
        String output = this.getAttribute(ATTR_OUTPUT, String.class);
        return output != null ? output : DEFAULT_OUTPUT;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributesWrapper.getAttributes().get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributesWrapper.getAttributes().set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributesWrapper.getAttributes().remove(key);
    }

    @Override
    public Attributes getAttributes() {
        return attributesWrapper.getAttributes();
    }

    @Override
    public void setAttributes(Attributes attributes) {
        this.attributesWrapper.setAttributes(attributes);
    }

    /**
     * adds listener.
     *
     * @param listener listener
     */
    public void addListener(OutputTemplateListener listener) {
        listenerSupport.addListener(listener);
    }

    /**
     * removes listener.
     *
     * @param listener listener
     */
    public void removeListener(OutputTemplateListener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public String toString() {
        return name;
    }
}
