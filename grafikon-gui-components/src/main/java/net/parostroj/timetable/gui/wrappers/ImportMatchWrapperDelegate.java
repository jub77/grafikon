package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.imports.ImportMatch;

/**
 * Import match delegate.
 *
 * @author jub
 */
public class ImportMatchWrapperDelegate implements WrapperDelegate<Object> {

    @Override
    public String toString(Object element) {
        ImportMatch match = (ImportMatch) element;
        return ResourceLoader.getString(match.getKey());
    }

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }

}
