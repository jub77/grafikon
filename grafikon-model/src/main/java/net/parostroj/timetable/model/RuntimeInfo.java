package net.parostroj.timetable.model;

import java.util.function.Consumer;
import net.parostroj.timetable.model.events.AttributeChange;

public class RuntimeInfo implements AttributesHolder {

    public static final String ATTR_FILE_VERSION = "file.version";
    public static final String ATTR_FILE = "file";

    private final Attributes attributes;

    RuntimeInfo(Consumer<AttributeChange> listener) {
        this.attributes = new Attributes((attrs, change) -> listener.accept(change));
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }
}
