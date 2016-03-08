package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Localization;
import net.parostroj.timetable.model.Localization.Translation;

/**
 * Localization key storage.
 *
 * @author jub
 */
@XmlType(propOrder = {"key", "translations"})
public class LSLocalizationItem {

    private String key;
    private List<LSLocalizationTranslation> translations;

    public LSLocalizationItem() {
    }

    public LSLocalizationItem(String key, Collection<Translation> translations) {
        this.key = key;
        if (translations != null) {
            this.translations = new ArrayList<LSLocalizationTranslation>();
            for (Translation translation : translations) {
                this.translations.add(new LSLocalizationTranslation(translation));
            }
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlElement(name = "trans")
    public List<LSLocalizationTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<LSLocalizationTranslation> translations) {
        this.translations = translations;
    }

    public void createTranslations(Localization localization) {
        localization.addKey(key);
        if (translations != null) {
            for (LSLocalizationTranslation translation : translations) {
                Translation t = translation.createTranslation(localization);
                localization.addTranslation(key, t);
            }
        }
    }
}
