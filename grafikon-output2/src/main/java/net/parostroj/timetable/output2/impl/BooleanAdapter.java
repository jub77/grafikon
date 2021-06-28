package net.parostroj.timetable.output2.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * False is transformed into empty string.
 *
 * @author jub
 */
public class BooleanAdapter extends XmlAdapter<String, Boolean> {

    @Override
    public Boolean unmarshal(String v) throws Exception {
        return Boolean.parseBoolean(v);
    }

    @Override
    public String marshal(Boolean v) throws Exception {
        return Boolean.TRUE.equals(v) ? "true" : null;
    }

}
