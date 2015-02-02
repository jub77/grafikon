package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.imports.ImportComponent;

/**
 * Import component delegate.
 *
 * @author jub
 */
public class ImportComponentWrapperDelegate implements WrapperDelegate<Object> {

    @Override
    public String toString(Object element) {
        ImportComponent component = (ImportComponent) element;
        return ResourceLoader.getString(component.getKey());
    }

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }

}
