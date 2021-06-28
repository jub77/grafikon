package net.parostroj.timetable.output2.impl;

import java.util.Locale;

import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.Company;

/**
 * Company information.
 *
 * @author jub
 */
@XmlType(propOrder = {"abbr", "name", "part", "locale"})
public class CompanyInfo {

    private String abbr;
    private String name;
    private String part;
    private Locale locale;

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public static CompanyInfo convert(Company company) {
        CompanyInfo info = new CompanyInfo();
        info.setAbbr(company.getAbbr());
        info.setName(company.getName());
        info.setPart(company.getAttribute(Company.ATTR_PART_NAME, String.class));
        info.setLocale(company.getLocale());
        return info;
    }
}
