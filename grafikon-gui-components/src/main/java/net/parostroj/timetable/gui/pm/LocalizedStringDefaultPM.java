package net.parostroj.timetable.gui.pm;

import java.util.Collection;
import java.util.Locale;

import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.LocalizedString;

/**
 * Presentation model for localized string with additional property with text for default locale.
 *
 * @author jub
 */
public class LocalizedStringDefaultPM extends LocalizedStringPM implements IPM<LocalizedString> {

    final ITextPM current;
    private boolean defaultStringIsCurrent;

    public LocalizedStringDefaultPM() {
        current = new TextPM();
        current.setEditable(false);
        string.addPropertyChangeListener("text", evt -> {
            if (defaultStringIsCurrent) {
                current.setText(string.getText());
            }
        });
        PMManager.setup(this);
    }

    @Override
    public void init(LocalizedString string) {
        super.init(string);
        this.addListenerToCurrentLocaleString(string);
    }

    @Override
    public LolizationEditResult init(LocalizedString string, Collection<Locale> availableLocales) {
        LolizationEditResult result = super.init(string, availableLocales);
        addListenerToCurrentLocaleString(string);
        return result;
    }

    private void addListenerToCurrentLocaleString(LocalizedString string) {
        if (string != null) {
            defaultStringIsCurrent = true;

            Locale defaultLocale = LocalizedString.getOnlyLanguageLocale(Locale.getDefault());
            for (StringWithLocalePM stringPM : strings) {
                if (defaultLocale.equals(stringPM.getUsedLocale())) {
                    defaultStringIsCurrent = false;
                    stringPM.string.addPropertyChangeListener("text", evt -> {
                        defaultStringIsCurrent = stringPM.string.isEmpty();
                        current.setText(defaultStringIsCurrent ?
                                this.string.getText() :
                                stringPM.string.getText());
                    });
                    break;
                }
            }
            this.current.setText(string.translate());
        } else {
            this.current.setText("");
        }
    }
}
