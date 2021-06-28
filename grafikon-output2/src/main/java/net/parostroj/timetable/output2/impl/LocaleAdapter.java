package net.parostroj.timetable.output2.impl;

import java.util.Locale;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter for locale.
 *
 * @author jub
 */
public class LocaleAdapter extends XmlAdapter<String, Locale> {

    @Override
    public Locale unmarshal(String v) throws Exception {
        return Locale.forLanguageTag(v);
    }

    @Override
    public String marshal(Locale v) throws Exception {
        return v.toLanguageTag();
    }

}
