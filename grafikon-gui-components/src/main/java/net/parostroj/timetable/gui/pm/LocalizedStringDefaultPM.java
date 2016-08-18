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
public class LocalizedStringDefaultPM extends LocalizedStringPM {

    final ITextPM stringWithCurrentLocale;
    private boolean defaultStringIsCurrent;

    public LocalizedStringDefaultPM() {
        stringWithCurrentLocale = new TextPM();
        stringWithCurrentLocale.setEditable(false);
        string.addPropertyChangeListener("text", evt -> {
            if (defaultStringIsCurrent) {
                stringWithCurrentLocale.setText(string.getText());
            }
        });
        PMManager.setup(this);
    }

    @Override
    public LolizationEditResult init(LocalizedString string, Collection<Locale> availableLocales) {
        LolizationEditResult result = super.init(string, availableLocales);

        if (string != null) {
            defaultStringIsCurrent = true;

            Locale defaultLocale = LocalizedString.getOnlyLanguageLocale(Locale.getDefault());
            for (StringWithLocalePM stringPM : strings) {
                if (defaultLocale.equals(stringPM.getUsedLocale())) {
                    defaultStringIsCurrent = false;
                    stringPM.string.addPropertyChangeListener("text", evt -> {
                        defaultStringIsCurrent = stringPM.string.isEmpty();
                        stringWithCurrentLocale.setText(defaultStringIsCurrent ?
                                this.string.getText() :
                                stringPM.string.getText());
                    });
                    break;
                }
            }

            this.stringWithCurrentLocale.setText(string.translate());
        } else {
            this.stringWithCurrentLocale.setText("");
        }

        return result;
    }
}
