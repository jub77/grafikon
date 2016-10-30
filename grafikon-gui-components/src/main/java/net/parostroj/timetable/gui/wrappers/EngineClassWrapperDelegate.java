package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.EngineClass;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class EngineClassWrapperDelegate extends BasicWrapperDelegate<EngineClass> {

    public EngineClassWrapperDelegate() {
    }

    @Override
    protected String toCompareString(EngineClass element) {
        return element.getName();
    }

    @Override
    public String toString(EngineClass element) {
        return element.getGroupKey() == null ? element.getName() : String.format("%s [%s]", element.getName(), element.getGroupKey());
    }

    @Override
    public int compare(EngineClass o1, EngineClass o2) {
        String groupKey1 = o1.getGroupKey();
        String groupKey2 = o2.getGroupKey();
        int result = getCollator().compare(groupKey1 == null ? "" : groupKey1, groupKey2 == null ? "" : groupKey2);
        return result == 0 ? super.compare(o1, o2) : result;
    }
}
