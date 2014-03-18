package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Output template.
 * 
 * @author jub
 */
public class OutputTemplate implements ObjectWithId, Visitable, AttributesHolder, OutputTemplateAttributes {
    
    private final String id;
    private final TrainDiagram diagram;

    private String name;
    private TextTemplate template;
    
    private Attributes attributes;
    private GTListenerSupport<OutputTemplateListener, OutputTemplateEvent> listenerSupport;
    private AttributesListener attributesListener;

    public OutputTemplate(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.setAttributes(new Attributes());
        listenerSupport = new GTListenerSupport<OutputTemplateListener, OutputTemplateEvent>(new GTEventSender<OutputTemplateListener, OutputTemplateEvent>() {

            @Override
            public void fireEvent(OutputTemplateListener listener, OutputTemplateEvent event) {
                listener.outputTemplateChanged(event);
            }
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
    
    public TrainDiagram getDiagram() {
        return diagram;
    }
    
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        this.listenerSupport.fireEvent(new OutputTemplateEvent(this, new AttributeChange("name", oldName, name)));
    }
    
    public String getName() {
        return name;
    }

    public void setTemplate(TextTemplate template) {
        TextTemplate oldTemplate = this.template;
        this.template = template;
        this.listenerSupport.fireEvent(new OutputTemplateEvent(this, new AttributeChange("template", oldTemplate, template)));
    }

    public TextTemplate getTemplate() {
        return template;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
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

    @Override
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {
            
            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                listenerSupport.fireEvent(new OutputTemplateEvent(OutputTemplate.this, change));
            }
        };
        this.attributes.addListener(attributesListener);
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

}
