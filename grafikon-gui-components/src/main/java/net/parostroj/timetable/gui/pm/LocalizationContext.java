package net.parostroj.timetable.gui.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class LocalizationContext<T extends Reference<LocalizedString>> implements Iterable<LocalizationType<T>> {

    private final Collection<LocalizationType<T>> localizationTypes;

    public LocalizationContext(Collection<? extends LocalizationType<T>> localizationTypes) {
        this.localizationTypes = new ArrayList<>(localizationTypes);
    }

    public Collection<LocalizationType<T>> getLocalizationTypes() {
        return localizationTypes;
    }

    public void writeBack() {
        for (LocalizationType<T> type : localizationTypes) {
            type.writeBack();
        }
    }

    @Override
    public Iterator<LocalizationType<T>> iterator() {
        return localizationTypes.iterator();
    }
}
