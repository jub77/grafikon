package net.parostroj.timetable.model;

import java.util.function.Consumer;
import net.parostroj.timetable.model.events.AttributeChange;

public class RuntimeInfo implements AttributesHolder {

    public static final String ATTR_FILE_VERSION = "file.version";
    public static final String ATTR_FILE = "file";
    public static final String ATTR_DIAGRAM_TYPE = "diagram.type";
    public static final String ATTR_TEMPLATE_STORAGE = "template.storage";

    private final Attributes attributes;

    RuntimeInfo(Consumer<AttributeChange> listener) {
        this.attributes = new Attributes((attrs, change) -> listener.accept(change));

        attributes.setSkipListeners(true);
        // NORMAL type is default
        attributes.set(ATTR_DIAGRAM_TYPE, TrainDiagramType.NORMAL);
        attributes.set(ATTR_TEMPLATE_STORAGE, ObjectMapping.fromFunction(id -> null));
        attributes.setSkipListeners(false);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public TrainDiagramType getDiagramType() {
        return attributes.get(ATTR_DIAGRAM_TYPE, TrainDiagramType.class);
    }

    @SuppressWarnings("unchecked")
    public ObjectMapping<OutputTemplate> getTemplateStorage() {
        return attributes.get(ATTR_TEMPLATE_STORAGE, ObjectMapping.class);
    }
}
