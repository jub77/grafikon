package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import net.parostroj.timetable.model.Localization;
import net.parostroj.timetable.model.Localization.Translation;

/**
 * Storage for translations.
 *
 * @author jub
 */
public class LSLocalizationTranslation {

    private String locale;
    private String text;

    public LSLocalizationTranslation() {
    }

    public LSLocalizationTranslation(Translation translation) {
        locale = translation.getLocale().toString();
        text = translation.getText();
    }

    @XmlAttribute
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @XmlValue
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Translation createTranslation(Localization localization) {
        return new Translation(localization.getLocale(locale), text);
    }
}
