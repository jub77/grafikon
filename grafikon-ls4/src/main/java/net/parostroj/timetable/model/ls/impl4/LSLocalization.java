package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.*;

import net.parostroj.timetable.model.Localization;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.utils.LocaleUtils;

/**
 * Storage for localization.
 *
 * @author jub
 */
@XmlRootElement(name = "localization")
@XmlType(propOrder = {"locales", "items"})
public class LSLocalization {

    private List<String> locales;
    private List<LSLocalizationItem> items;

    public LSLocalization() {
    }

    public LSLocalization(Localization localization) {
        locales = new ArrayList<String>();
        for (Locale locale : localization.getLocales()) {
            locales.add(locale.toString());
        }
        items = new ArrayList<LSLocalizationItem>();
        for (String key : localization.getKeys()) {
            items.add(new LSLocalizationItem(key, localization.getTranslations(key)));
        }
    }

    @XmlElement(name = "locale")
    public List<String> getLocales() {
        return locales;
    }

    public void setLocales(List<String> locales) {
        this.locales = locales;
    }

    public List<LSLocalizationItem> getItems() {
        return items;
    }

    @XmlElement(name = "item")
    public void setItems(List<LSLocalizationItem> items) {
        this.items = items;
    }

    public void createLocalization(TrainDiagram diagram) throws LSException {
        Localization localization = diagram.getLocalization();
        if (locales != null) {
            for (String locale : locales) {
                localization.addLocale(LocaleUtils.parseLocale(locale));
            }
        }
        if (items != null) {
            for (LSLocalizationItem item : items) {
                item.createTranslations(localization);
            }
        }
    }
}
