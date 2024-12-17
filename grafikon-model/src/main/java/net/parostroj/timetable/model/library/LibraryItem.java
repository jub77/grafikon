package net.parostroj.timetable.model.library;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;

public class LibraryItem implements AttributesHolder {

    private static final String ATTR_DESCRIPTION = "description";

    private final LibraryItemType type;
    private final ObjectWithId object;

    private final Attributes attributes;

    LibraryItem(LibraryItemType type, ObjectWithId object) {
        this.type = type;
        this.object = object;
        this.attributes = new Attributes();
    }

    public ObjectWithId getObject() {
        return object;
    }

    public LibraryItemType getType() {
        return type;
    }

    public String getName() {
        return switch (type) {
            case NODE -> ((Node) object).getName();
            case OUTPUT_TEMPLATE -> ((OutputTemplate) object).getKey();
            case ENGINE_CLASS -> ((EngineClass) object).getName();
            case TRAIN_TYPE -> ((TrainType) object).getDefaultAbbr();
            case LINE_CLASS -> ((LineClass) object).getName();
            case TRAIN_TYPE_CATEGORY -> ((TrainTypeCategory) object).getKey();
        };
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
        return String.format("%s -> %s", type, object);
    }
}
