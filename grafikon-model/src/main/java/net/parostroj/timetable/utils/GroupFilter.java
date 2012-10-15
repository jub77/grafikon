package net.parostroj.timetable.utils;

import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.Group;

public class GroupFilter<T extends AttributesHolder> implements Filter<T> {

    private final Class<T> clazz;
    private final Group group;

    public GroupFilter(Class<T> clazz, Group group) {
        this.clazz = clazz;
        this.group = group;
    }

    @Override
    public boolean is(Object item) {
        T t = this.get(item);
        Group foundGroup = t.getAttributes().get("group", Group.class);
        if (group == null)
            return foundGroup == null;
        else
            return group.equals(foundGroup);
    }

    @Override
    public T get(Object item) {
        return clazz.cast(item);
    }

}
