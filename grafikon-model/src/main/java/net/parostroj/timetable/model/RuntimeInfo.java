package net.parostroj.timetable.model;

import java.util.function.Consumer;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.templates.OutputTemplateStorage;

public class RuntimeInfo implements AttributesHolder {

    public static final String ATTR_FILE_VERSION = "file.version";
    public static final String ATTR_FILE = "file";
    public static final String ATTR_DIAGRAM_TYPE = "diagram.type";
    public static final String ATTR_TEMPLATE_MAPPING = "template.mapping";

    private final Attributes attributes;

    RuntimeInfo(Consumer<AttributeChange> listener) {
        this.attributes = new Attributes((attrs, change) -> listener.accept(change));

        attributes.setSkipListeners(true);
        // NORMAL type is default
        this.setDiagramType(TrainDiagramType.NORMAL);
        this.setTemplateMapping(OutputTemplateMapping.createEmpty());
        attributes.setSkipListeners(false);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public TrainDiagramType getDiagramType() {
        return attributes.get(ATTR_DIAGRAM_TYPE, TrainDiagramType.class);
    }

    public Permissions getPermissions() {
        return Permissions.forType(getDiagramType());
    }

    public void setDiagramType(TrainDiagramType diagramType) {
        attributes.setRemove(ATTR_DIAGRAM_TYPE, diagramType);
    }

    public OutputTemplateMapping getTemplateMapping() {
        return attributes.get(ATTR_TEMPLATE_MAPPING, OutputTemplateMapping.class);
    }

    public void setTemplateMapping(OutputTemplateMapping storage) {
        attributes.setRemove(ATTR_TEMPLATE_MAPPING, storage);
    }

    public OutputTemplateStorage getTemplateStorage() {
        OutputTemplateMapping mapping = attributes.get(ATTR_TEMPLATE_MAPPING, OutputTemplateMapping.class);
        return mapping instanceof OutputTemplateStorage ? (OutputTemplateStorage) mapping : null;
    }
}
