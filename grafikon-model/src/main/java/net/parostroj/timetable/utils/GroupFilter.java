package net.parostroj.timetable.utils;

import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.Group;

/**
 * Filter by groups. If the group is null, it returns true for items with no
 * group specified.
 *
 * @author jub
 */
public class GroupFilter<T extends AttributesHolder> implements Filter<T, T> {

    private final Group group;

    public GroupFilter(Group group) {
        this.group = group;
    }

    @Override
    public boolean is(T item) {
        T t = this.get(item);
        Group foundGroup = t.getAttributes().get("group", Group.class);
        if (group == null)
            return foundGroup == null;
        else
            return group.equals(foundGroup);
    }

    @Override
    public T get(T item) {
        return item;
    }

}
