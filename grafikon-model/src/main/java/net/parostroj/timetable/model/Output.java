package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.ObservableObject;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Output based on OutputTemplate - separate settings.
 *
 * @author jub
 */
public class Output implements ObjectWithId, AttributesHolder, ObservableObject, Visitable, TrainDiagramPart {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_TEMPLATE = "template";
    public static final String ATTR_TEMPLATE_REFERENCE = "template.reference";
    public static final String ATTR_SELECTION = "selection";
    public static final String ATTR_LOCALE = "locale";
    public static final String ATTR_KEY = "key";

    public static final String CATEGORY_SETTINGS = "settings";

    private final String id;
    private final TrainDiagram diagram;

    private final Attributes attributes;
    private final ListenerSupport listenerSupport;

    Output(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.listenerSupport = new ListenerSupport();
        this.attributes = new Attributes((attrs, change) -> listenerSupport.fireEvent(new Event(Output.this, change)));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public OutputTemplate getTemplate() {
        return attributes.get(ATTR_TEMPLATE, OutputTemplate.class);
    }

    public void setTemplate(OutputTemplate template) {
        attributes.remove(ATTR_TEMPLATE_REFERENCE);
        attributes.setRemove(ATTR_TEMPLATE, template);
    }

    @SuppressWarnings("unchecked")
    public ObjectReference<OutputTemplate> getTemplateRef() {
        return attributes.get(ATTR_TEMPLATE_REFERENCE, ObjectReference.class);
    }

    public void setTemplateRef(ObjectReference<OutputTemplate> templateRef) {
        attributes.remove(ATTR_TEMPLATE);
        attributes.setRemove(ATTR_TEMPLATE_REFERENCE, templateRef);
    }

    public OutputTemplate getOutputTemplate() {
        OutputTemplate template = getTemplate();
        if (template == null) {
            ObjectReference<OutputTemplate> templateRef = getTemplateRef();
            if (templateRef != null) {
                template = templateRef.getObject(diagram.getRuntimeInfo().getTemplateStorage()::getTemplateById);
            }
        }
        return template;
    }

    public LocalizedString getName() {
        return attributes.get(ATTR_NAME, LocalizedString.class);
    }

    public void setName(LocalizedString name) {
        attributes.setRemove(ATTR_NAME, name);
    }

    public Collection<ObjectWithId> getSelection() {
        return attributes.getAsCollection(ATTR_SELECTION, ObjectWithId.class);
    }

    public void setSelection(Collection<? extends ObjectWithId> selection) {
        attributes.setRemove(ATTR_SELECTION, selection);
    }

    public Locale getLocale() {
        return attributes.get(ATTR_LOCALE, Locale.class);
    }

    public void setLocale(Locale locale) {
        attributes.setRemove(ATTR_LOCALE, locale);
    }

    public String getKey() {
        return attributes.get(ATTR_KEY, String.class);
    }

    public void setKey(String key) {
        attributes.setRemove(ATTR_KEY, key);
    }

    /**
     * @return attributes containing settings category combined from template and this output
     */
    public Attributes getSettings() {
        Attributes combinedAttributes = new Attributes();
        OutputTemplate ot = getTemplate();
        if (ot != null) {
            combinedAttributes.addAttributesMap(ot.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_SETTINGS),
                    CATEGORY_SETTINGS);
            combinedAttributes.add(attributes, CATEGORY_SETTINGS);
        }
        return combinedAttributes;
    }

    /**
     * @param combinedAttributes attributes that should be stored (values that equals values in template are ignored
     *                  and removed)
     */
    public void setSettings(Attributes combinedAttributes) {
        OutputTemplate ot = getTemplate();
        // if there is no template ignore all values
        if (ot != null) {
            Attributes templateAttributes = ot.getAttributes();
            for (Entry<String, Object> entry : combinedAttributes.getAttributesMap(CATEGORY_SETTINGS).entrySet()) {
                String attributeName = entry.getKey();
                Object attributeValue = entry.getValue();
                Object templateValue = templateAttributes.get(OutputTemplate.CATEGORY_SETTINGS, attributeName);
                if (Objects.equals(templateValue, attributeValue)) {
                    attributes.remove(CATEGORY_SETTINGS, attributeName);
                } else {
                    attributes.set(CATEGORY_SETTINGS, attributeName, attributeValue);
                }
            }
        }
    }

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getName() != null ? getName().getDefaultString() : "<none>";
    }
}
