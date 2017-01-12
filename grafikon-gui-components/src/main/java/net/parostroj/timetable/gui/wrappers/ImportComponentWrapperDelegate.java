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
        return ResourceLoader.getString("import." + component.getKey());
    }
}
