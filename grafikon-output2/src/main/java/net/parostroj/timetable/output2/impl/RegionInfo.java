package net.parostroj.timetable.output2.impl;

import java.util.Locale;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.Region;

/**
 * Region information.
 *
 * @author jub
 */
@XmlType(propOrder = {"name", "locale"})
public class RegionInfo {

    private String name;
    private Locale locale;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public static RegionInfo convert(Region region) {
        RegionInfo info = new RegionInfo();
        info.setName(region.getName());
        info.setLocale(region.getAttribute(Region.ATTR_LOCALE, Locale.class));
        return info;
    }
}
