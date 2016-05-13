package net.parostroj.timetable.gui.pm;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.beanfabrics.Path;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.SortKey;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.LocalizedString.Builder;
import net.parostroj.timetable.model.LocalizedString.StringWithLocale;

/**
 * Presentation model for localized string.
 *
 * @author jub
 */
public class LocalizedStringPM extends AbstractPM {

    private class EditResultImpl implements EditResult {

        private LocalizedString builtResult;

        @Override
        public LocalizedString get() {
            return builtResult == null ? build() : builtResult;
        }

        public void finishBuild() {
            this.builtResult = build();
        }

        public LocalizedString build() {
            Builder builder = LocalizedString.newBuilder(string.getText());
            for (StringWithLocalePM swl : strings) {
                StringWithLocale stringWithLocale = swl.build();
                if (stringWithLocale != null) {
                    builder.addStringWithLocale(stringWithLocale);
                }
            }
            return builder.build();
        }
    }

    final ListPM<StringWithLocalePM> strings;
    final ITextPM string;

    private EditResultImpl currentResult;

    public LocalizedStringPM() {
        strings = new ListPM<>();
        string = new TextPM();
        string.setEditable(false);
        PMManager.setup(this);
    }

    public EditResult init(LocalizedString string, Collection<Locale> availableLocales) {
        if (currentResult != null) {
            currentResult.finishBuild();
            currentResult = null;
        }
        if (string == null) {
            this.strings.clear();
            this.string.setText(null);
            this.string.setEditable(false);
            return null;
        }
        this.string.setEditable(true);
        currentResult = new EditResultImpl();
        Collection<Locale> stringLocales = string.getLocales();

        Set<Locale> locales = new HashSet<>(availableLocales);
        locales.addAll(stringLocales);

        strings.clear();

        for (Locale locale : locales) {
            String text = string.getLocalizedString(locale);
            strings.add(new StringWithLocalePM(text, locale));
        }

        this.string.setText(string.getDefaultString());
        strings.sortBy(Arrays.asList(new SortKey(true, new Path("locale"))));
        return currentResult;
    }
}
