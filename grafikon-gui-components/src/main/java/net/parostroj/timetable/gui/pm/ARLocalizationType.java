package net.parostroj.timetable.gui.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import net.parostroj.timetable.gui.wrappers.WrapperConversion;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.AttributeReference;

public abstract class ARLocalizationType<T extends AttributeReference<LocalizedString>> extends LocalizationType<T> {

    private Collection<T> toBeRemoved;

    public ARLocalizationType(String desciption, Collection<? extends T> strings, WrapperConversion<T> conversion,
            Collection<Locale> locales) {
        super(desciption, strings, conversion, locales);
        toBeRemoved = new HashSet<>();
    }

    protected abstract T createImpl(String key);

    public void addToRemove(T ref) {
        this.removeEdited(ref);
        this.toBeRemoved.add(ref);
    }

    public T createNew(String key) {
        T newRef = this.createImpl(key);
        toBeRemoved.remove(newRef);
        return newRef;
    }

    @Override
    public void writeBack() {
        super.writeBack();
        for (T item : toBeRemoved) {
            item.remove();
        }
    }
}
