package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.imports.ImportComponent;

/**
 * Import component delegate.
 *
 * @author jub
 */
public class ImportComponentWrapperDelegate implements WrapperDelegate<ImportComponent> {

    @Override
    public String toString(ImportComponent element) {
        ImportComponent component = element;
        return ResourceLoader.getString(component.getKey());
    }

    @Override
    public int compare(ImportComponent o1, ImportComponent o2) {
        return 0;
    }

}
