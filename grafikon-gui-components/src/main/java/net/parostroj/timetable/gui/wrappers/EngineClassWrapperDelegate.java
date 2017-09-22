package net.parostroj.timetable.gui.wrappers;

import java.util.Comparator;

import net.parostroj.timetable.model.EngineClass;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class EngineClassWrapperDelegate extends BasicWrapperDelegate<EngineClass> {

    @Override
    public String toCompareString(EngineClass element) {
        return element.getName();
    }

    @Override
    public String toString(EngineClass element) {
        return element.getGroupKey() == null ? element.getName() : String.format("%s [%s]", element.getName(), element.getGroupKey());
    }

    @Override
    public Comparator<? super EngineClass> getComparator() {
        return this::compare;
    }

    private int compare(EngineClass o1, EngineClass o2) {
        if (o1 == null) {
            if (o2 == null) return 0;
            else return -1;
        }
        if (o2 == null) return 1;
        String groupKey1 = o1.getGroupKey();
        String groupKey2 = o2.getGroupKey();
        int result = getCollator().compare(groupKey1 == null ? "" : groupKey1, groupKey2 == null ? "" : groupKey2);
        return result == 0 ? getCollator().compare(toCompareString(o1), toCompareString(o2)) : result;
    }
}
