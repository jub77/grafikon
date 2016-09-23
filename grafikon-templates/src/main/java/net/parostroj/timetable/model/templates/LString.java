package net.parostroj.timetable.model.templates;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.FluentIterable;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.LocalizedString.StringWithLocale;

public class LString {

    private String defaultString;
    private List<LStringLang> localizedStrings;

    public LString() {
    }

    public LString(LocalizedString ls) {
        this.setDefaultString(ls.getDefaultString());
        for (StringWithLocale swl : ls.getLocalizedStrings()) {
            this.getLocalizedStrings().add(new LStringLang(swl.getLocale().toLanguageTag(), swl.getString()));
        }
    }

    @XmlElement(name = "default")
    public String getDefaultString() {
        return defaultString;
    }

    public void setDefaultString(String defaultString) {
        this.defaultString = defaultString;
    }

    @XmlElement(name = "localized")
    public List<LStringLang> getLocalizedStrings() {
        if (localizedStrings == null) {
            localizedStrings = new ArrayList<>();
        }
        return localizedStrings;
    }

    public void setLocalizedStrings(List<LStringLang> localizedStrings) {
        this.localizedStrings = localizedStrings;
    }

    public LocalizedString createLocalizedString() {
        return LocalizedString.newBuilder(getDefaultString())
                .addAllStringWithLocale(FluentIterable
                        .from(getLocalizedStrings())
                        .transform(ls -> LocalizedString.newStringWithLocale(null, null))
                        .toList())
                .build();
    }
}
