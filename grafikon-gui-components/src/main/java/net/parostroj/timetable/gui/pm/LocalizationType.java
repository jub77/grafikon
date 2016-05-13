package net.parostroj.timetable.gui.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class LocalizationType {

    private String description;
    private Collection<? extends Reference<LocalizedString>> strings;
    private WrapperConversion<Reference<LocalizedString>> conversion;
    private Collection<EditedLocalizedString> allEdited;
    private Collection<Locale> locales;

    public LocalizationType(String desciption, Collection<? extends Reference<LocalizedString>> strings,
            WrapperConversion<Reference<LocalizedString>> conversion, Collection<Locale> locales) {
        this.description = desciption;
        this.strings = strings;
        this.conversion = conversion;
        this.locales = locales;
        this.allEdited = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public Collection<? extends Reference<LocalizedString>> getStrings() {
        return strings;
    }

    public WrapperConversion<Reference<LocalizedString>> getConversion() {
        return conversion;
    }

    public Collection<Locale> getLocales() {
        return locales;
    }

    public Collection<EditedLocalizedString> getEdited() {
        return allEdited;
    }

    public LocalizedString getNewOrEdited(Reference<LocalizedString> ref) {
        // check existing
        for (EditedLocalizedString edited : allEdited) {
            if (edited.getReference() == ref) {
                return edited.getEdited().get();
            }
        }
        return ref.get();
    }

    public void addOrUpdateEdited(Reference<LocalizedString> ref, EditResult result) {
        for (EditedLocalizedString edited : allEdited) {
            if (edited.getReference() == ref) {
                edited.updateEdited(result);
                return;
            }
        }
        allEdited.add(new EditedLocalizedString(ref, result));
    }

    public void writeBack() {
        for (EditedLocalizedString edited : allEdited) {
            edited.writeBack();
        }
    }
}
