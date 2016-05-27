package net.parostroj.timetable.model.library;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;

public class LibraryItem implements AttributesHolder {

    private static final String ATTR_DESCRIPTION = "description";

    private final LibraryItemType type;
    private final ObjectWithId item;

    private final Attributes attributes;

    LibraryItem(LibraryItemType type, ObjectWithId item) {
        this.type = type;
        this.item = item;
        this.attributes = new Attributes();
    }

    public ObjectWithId getItem() {
        return item;
    }

    public LibraryItemType getType() {
        return type;
    }

    public String getName() {
        switch (type) {
        case NODE: return ((Node) item).getName();
        case OUTPUT_TEMPLATE: return ((OutputTemplate) item).getName();
        case ENGINE_CLASS: return ((EngineClass) item).getName();
        default: return null;
        }
    }

    public LocalizedString getDescription() {
        return attributes.get(ATTR_DESCRIPTION, LocalizedString.class);
    }

    public void setDescription(LocalizedString description) {
        attributes.setRemove(ATTR_DESCRIPTION, description);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", type, item);
    }
}
