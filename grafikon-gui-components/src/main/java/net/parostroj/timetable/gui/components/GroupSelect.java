package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.model.Group;

public class GroupSelect {

    public static enum Type {
        ALL, NONE, GROUP
    }

    private final Type type;
    private final Group group;

    public GroupSelect(Type type, Group group) {
        super();
        this.type = type;
        this.group = group;
    }

    public GroupSelect(Type type) {
        this(type, null);
    }

    public Group getGroup() {
        return group;
    }

    public Type getType() {
        return type;
    }
}
