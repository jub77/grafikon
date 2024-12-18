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
        this.setDiagramType(TrainDiagramType.NORMAL);
        this.setTemplateStorage(OutputTemplateStorage.createEmpty());
        attributes.setSkipListeners(false);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public TrainDiagramType getDiagramType() {
        return attributes.get(ATTR_DIAGRAM_TYPE, TrainDiagramType.class);
    }

    public void setDiagramType(TrainDiagramType diagramType) {
        attributes.setRemove(ATTR_DIAGRAM_TYPE, diagramType);
    }

    public OutputTemplateStorage getTemplateStorage() {
        return attributes.get(ATTR_TEMPLATE_STORAGE, OutputTemplateStorage.class);
    }

    public void setTemplateStorage(OutputTemplateStorage storage) {
        attributes.setRemove(ATTR_TEMPLATE_STORAGE, storage);
    }
}
