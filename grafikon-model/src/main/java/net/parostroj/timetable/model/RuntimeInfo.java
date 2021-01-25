package net.parostroj.timetable.model;

import java.util.function.Consumer;
import net.parostroj.timetable.model.events.AttributeChange;

public class RuntimeInfo implements AttributesHolder {

    public static final String ATTR_LOADED_VERSION = "loaded.version";

    private final Attributes attributes;

    RuntimeInfo(Consumer<AttributeChange> listener) {
        this.attributes = new Attributes((attrs, change) -> listener.accept(change));
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }
}
