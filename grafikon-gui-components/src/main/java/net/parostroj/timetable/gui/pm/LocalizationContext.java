package net.parostroj.timetable.gui.pm;

import java.util.Collection;
import java.util.Iterator;

public class LocalizationContext implements Iterable<LocalizationType> {

    private final Collection<LocalizationType> localizationTypes;

    public LocalizationContext(Collection<LocalizationType> localizationTypes) {
        this.localizationTypes = localizationTypes;
    }

    public Collection<LocalizationType> getLocalizationTypes() {
        return localizationTypes;
    }

    public void writeBack() {
        for (LocalizationType type : localizationTypes) {
            type.writeBack();
        }
    }

    @Override
    public Iterator<LocalizationType> iterator() {
        return localizationTypes.iterator();
    }
}
