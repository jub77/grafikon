package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Output template.
 *
 * @author jub
 */
public class OutputTemplate implements ObjectWithId, Visitable, AttributesHolder, TrainDiagramPart,
        ObservableObject, ObjectWithVersion {

    public static final String ATTR_OUTPUT = "output";
    public static final String ATTR_OUTPUT_TYPE = "output.type";
    public static final String ATTR_OUTPUT_EXTENSION = "output.extension";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_KEY = "key";
    public static final String ATTR_TEMPLATE = "template";
    public static final String ATTR_SCRIPT = "script";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_ATTACHMENT = "attachment";
    public static final String ATTR_SELECTION_TYPE = "selection.type";
    public static final String ATTR_VERSION = "version";

    public static final String CATEGORY_I18N = "localization";
    public static final String CATEGORY_SETTINGS = "settings";

    public static final String DEFAULT_OUTPUT = "groovy";

    private final String id;
    private final TrainDiagram diagram;

    private TextTemplate template;
    private Script script;

    private final ItemSet<Attachment> attachments;

    private final Attributes attributes;
    private final ListenerSupport listenerSupport;

    OutputTemplate(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        listenerSupport = new ListenerSupport();
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(OutputTemplate.this, change)));
        this.attachments = new ItemSetImpl<>((type, item) -> {
            AttributeChange change = switch (type) {
                case ADDED -> new AttributeChange(ATTR_ATTACHMENT, null, item);
                case REMOVED -> new AttributeChange(ATTR_ATTACHMENT, item, null);
                default -> throw new IllegalArgumentException("Unsupported type: " + type);
            };
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

    @Override
    public ModelVersion getVersion() {
        return getAttribute(ATTR_VERSION, ModelVersion.class, ModelVersion.initialModelVersion());
    }

    public LocalizedString getName() {
        return attributes.get(ATTR_NAME, LocalizedString.class);
    }

    public void setName(LocalizedString name) {
        attributes.setRemove(ATTR_NAME, name);
    }

    public String getKey() {
        return attributes.get(ATTR_KEY, String.class);
    }

    public void setKey(String key) {
        attributes.setRemove(ATTR_KEY, key);
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

    public LocalizedString getDescription() {
        return this.getAttribute(ATTR_DESCRIPTION, LocalizedString.class);
    }

    public ModelObjectType getSelectionType() {
        String selectionTypeKey = this.getAttribute(ATTR_SELECTION_TYPE, String.class);
        return selectionTypeKey == null ? null : ModelObjectType.getByKey(selectionTypeKey);
    }

    public void setSelectionType(ModelObjectType selectionType) {
        this.getAttributes().setRemove(ATTR_SELECTION_TYPE, selectionType == null ? null : selectionType.getKey());
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
        return getName() != null ? getName().translate() : getKey();
    }
}
