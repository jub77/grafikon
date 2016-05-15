package net.parostroj.timetable.gui.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class LocalizationType<T extends Reference<LocalizedString>> {

    private String description;
    private Collection<? extends T> strings;
    private WrapperConversion<T> conversion;
    private Collection<EditedLocalizedString> allEdited;
    private Collection<Locale> locales;

    public LocalizationType(String desciption, Collection<? extends T> strings,
            WrapperConversion<T> conversion, Collection<Locale> locales) {
        this.description = desciption;
        this.strings = strings;
        this.conversion = conversion;
        this.locales = locales;
        this.allEdited = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public Collection<? extends T> getStrings() {
        return strings;
    }

    public WrapperConversion<T> getConversion() {
        return conversion;
    }

    public Collection<Locale> getLocales() {
        return locales;
    }

    public Collection<EditedLocalizedString> getAllEdited() {
        return allEdited;
    }

    public EditedLocalizedString removeEdited(T ref) {
        for (EditedLocalizedString edited : allEdited) {
            if (edited.getReference() == ref) {
                return edited;
            }
        }
        return null;
    }

    public LocalizedString getNewOrEditedString(T ref) {
        // check existing
        for (EditedLocalizedString edited : allEdited) {
            if (edited.getReference() == ref) {
                return edited.getEdited().get();
            }
        }
        return ref.get();
    }

    public EditedLocalizedString addOrUpdateEdited(T ref, LolizationEditResult result) {
        for (EditedLocalizedString edited : allEdited) {
            if (edited.getReference() == ref) {
                edited.updateEdited(result);
                return edited;
            }
        }
        EditedLocalizedString newEdited = new EditedLocalizedString(ref, result);
        allEdited.add(newEdited);
        return newEdited;
    }

    public void writeBack() {
        for (EditedLocalizedString edited : allEdited) {
            edited.writeBack();
        }
    }
}
