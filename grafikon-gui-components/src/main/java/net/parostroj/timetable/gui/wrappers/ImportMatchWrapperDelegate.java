package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.imports.ImportMatch;

/**
 * Import match delegate.
 *
 * @author jub
 */
public class ImportMatchWrapperDelegate implements WrapperDelegate<ImportMatch> {

    @Override
    public String toString(ImportMatch element) {
        ImportMatch match = element;
        return ResourceLoader.getString(match.getKey());
    }

    @Override
    public int compare(ImportMatch o1, ImportMatch o2) {
        return 0;
    }

}
